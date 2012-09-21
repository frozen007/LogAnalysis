use threads;
use threads::shared;
use Thread::Queue;
use MongoDB;
use Getopt::Long;
require ("parsingformat.pl");

my $LogFormat="%host %other %other %time1 %methodurl %code %cost %bytesd %otherquot %otherquot";
my $LogSeparator=" ";
my $LogCostUnit="us";

#default MongoDB options
my $MongoHost="localhost";
my $MongoPort=27017;
my $MongoCollection="logstat";

my $Debug=0;
my $LogDetailDebug=0;

=options
#LogFormat      -format="xxxxxx"

#LogSeparator   -sep=" "

#LogCostUnit    -cu=us

#MongDB Option
    -mongohost  ip
    -mongoport  port
    -mongocol   collections

=cut
GetOptions(
    'format=s'=>\$LogFormat, 'sep=s'=>\$LogSeparator, 'cu=s'=>\$LogCostUnit, 
    'mongohost=s'=>\$MongoHost, 'mongoport=s'=>\$MongoPort, 'mongocol=s'=>\$MongoCollection, 
    'debug'=>\$Debug, 'logdebug'=>\$LogDetailDebug);

my $LogFile=shift;

=log thread
#initialize a queue and a thread for recording log to db
=cut
my $LogQueue = Thread::Queue->new();
my $thr = threads->create(sub {
    my $conn;
    my $logdb;
    my $logcoll;
    eval{
        $conn = MongoDB::Connection->new(host=>${MongoHost}, port=>${MongoPort});
        $logdb = $conn->logdb;
        $logcoll = $logdb->get_collection($MongoCollection);
    };
    my $canwork=1;
    THREAD_WHILE:while($canwork) {
        if ($Debug) {print "worker idle...\n";}
        my @log_arr_ref_list = [];
        {
            lock($LogQueue);
            my $pendingCnt = $LogQueue->pending();
        
            if($pendingCnt>0) {
                @log_arr_ref_list = $LogQueue->dequeue_nb($pendingCnt);
            } else {
                cond_wait($LogQueue);
                next THREAD_WHILE;
            }

        }
        foreach my $log_arr_ref (@log_arr_ref_list) {
            if(scalar(@{$log_arr_ref}) eq 0) {
                if ($Debug) {print "no log worker halted...\n";}
                $canwork=0;
                last THREAD_WHILE;
            }
            if($logcoll) {
                foreach my $log_ele (@{$log_arr_ref}) {
                    my $url = @{$log_ele}[0];
                    my $cost = int(@{$log_ele}[1]);
                    #{"url"=>$self->url, "cost"=>$self->cost}
                    $logcoll->insert({"url"=>$url, "cost"=>$cost});
                }
            }
            if($Debug) {print "records that worker processed:", scalar(@{$log_arr_ref}), "\n";}
        }
    }
    $logcoll->ensure_index({"cost"=>1});
});

&DefinePerlParsingFormat($LogFormat, $LogSeparator);

if ($Debug) {
    print "\npos_code:$pos_code\n";
    print "pos_cost:$pos_cost\n";
    print $PerlParsingFormat, "\n";
    foreach (@fieldlib) {
        print $_, "\t";
    }
    print "\n";
}


my $costKeyLen = 4;
my $costStatRecordLevel = 2;
my @costStatConfig = (0, 1, 3, 10);
=LogCostUnit
# us=microsecond
# ms=millisecond
# s=second
=cut
my $costUnitValue = 1;
if($LogCostUnit eq 'us') {
    $costUnitValue = 1000000;
} elsif($LogCostUnit eq 'ms') {
    $costUnitValue = 1000;
} else {
    $costUnitValue = 1;
}

for my $config_id (0..$#costStatConfig) {
    @costStatConfig[$config_id] *= $costUnitValue;
}

my @costKeyList = ("cost0_1s", "cost1_3s", "cost3_10s", "cost10s");

my %costmap = ();
my $totalrecord=0;
my $status500cnt=0;

my $log_arr_ref = [];

open(LOG, $LogFile) || die $!;
binmode LOG;
while(<LOG>) {
    my $line = $_;
    chomp $line;
    if($line eq '') {
        next;
    }
    $totalrecord++;

    @fieldList = map( /$PerlParsingFormat/, $line );

    my $code = $fieldList[$pos_code];
    if($code eq '500') {
        $status500cnt++;
    }

    $url  = $fieldList[$pos_url];
    $cost = $fieldList[$pos_cost];


=dispatch log record
#    my $httplog = HttpRequestLog->new(url=>$url, cost=>$cost);
=cut
    if($cost >= @costStatConfig[$costStatRecordLevel]) {
        my $httplog = [$url, $cost];
        push(@{$log_arr_ref}, $httplog);
    
        if($totalrecord % 1000 eq 0) {
            $LogQueue->enqueue($log_arr_ref);
            lock($LogQueue);
            $log_arr_ref = [];
            cond_signal($LogQueue);
        }
    }

    if($LogDetailDebug) {
        print "fieldList:";
        foreach my $f (@fieldList) {
            print $f, "\t";
        }
        print "\n";
        print "cost:", $cost, "\n";
    }

    my $costCnt;
    my $costKey;

    foreach my $index (0..$costKeyLen-1) {
        if($index < $costKeyLen-1) {
            if($cost >=@costStatConfig[$index] && $cost <@costStatConfig[$index+1]) {
                #match
            } else {
                next;
            }
        } else {
            if($cost >=@costStatConfig[$index]) {
                #match
            } else {
                next;
            }
        }

        $costKey = @costKeyList[$index];
        $costCnt = $costmap{$costKey};
        if($costCnt eq '') {
            $costCnt = 1;
        } else {
            $costCnt++;
        }
        $costmap{$costKey} = $costCnt;

        #break the loop and check the next record
        last;
    }

}
=final dispatch
=cut
{
    $LogQueue->enqueue($log_arr_ref);
    $log_arr_ref = [];
    $LogQueue->enqueue($log_arr_ref);
    lock($LogQueue);
    cond_signal($LogQueue);
}


if($Debug) {print "waiting for worker finish\n";}
$thr->join();


#output the result
print "logfile=$LogFile\n";
print "totalrecord=$totalrecord\n";
print "status500cnt=$status500cnt\n";
for my $key (keys %costmap) {
    print "$key=$costmap{$key}\n";
}

