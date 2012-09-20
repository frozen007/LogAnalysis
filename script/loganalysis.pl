use threads;
use threads::shared;
use Thread::Queue;
use MongoDB;
require ("parsingformat.pl");

my $LogServerName=shift;
my $LogFile=shift;
my $LogFormat=shift;
my $LogSeparator=shift;
my $LogCostUnit=shift;

#$Debug=1;

if($LogFormat eq '') {
    $LogFormat="%host %other %other %time1 %methodurl %code %cost %bytesd %otherquot %otherquot";
}
if($LogSeparator eq '') {
    $LogSeparator=" ";
}
if($LogCostUnit eq '') {
    $LogCostUnit="us";
}

my $MainThreadOver : shared = 0;

=log thread
#initialize a queue and a thread for recording log to db
=cut
my $LogQueue = Thread::Queue->new();
my $thr = threads->create(sub {
    my $conn = MongoDB::Connection->new(host=>'localhost', port=>27017);
    my $logdb = $conn->logdb;
    my $logcoll = $logdb->get_collection($LogServerName);
    THREAD_WHILE:while(1) {
        my @log_arr_ref_list = [];
        {
            lock($LogQueue);
            my $pendingCnt = $LogQueue->pending();
        
            if($pendingCnt>0) {
                @log_arr_ref_list = $LogQueue->dequeue_nb($pendingCnt);
            } else {
                if($MainThreadOver eq 1) {
                    print "no log...\n";                
                    last THREAD_WHILE;
                } else {
                    cond_wait($LogQueue);
                    next;
                }
            }
        }

        foreach my $log_arr_ref (@log_arr_ref_list) {
            foreach my $log_ele (@{$log_arr_ref}) {
                my $url = @{$log_ele}[0];
                my $cost = @{$log_ele}[1];
                #{"url"=>$self->url, "cost"=>$self->cost}
                $logcoll->insert({"url"=>$url, "cost"=>$cost});
            }
        }
        
    }
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

my @costStatConfig = ();
=LogCostUnit
# us=microsecond
# ms=millisecond
# s=second
=cut
if($LogCostUnit eq 'us') {
    @costStatConfig = (0, 1000000, 3000000, 10000000); #microsecond
} elsif($LogCostUnit eq 'ms') {
    @costStatConfig = (0, 1000, 3000, 10000); #millisecond
} else {
    @costStatConfig = (0, 1, 3, 10); #millisecond
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
    my $httplog = [$url, $cost];
    push(@{$log_arr_ref}, $httplog);

    if($totalrecord % 1000 eq 0) {
        $LogQueue->enqueue($log_arr_ref);
        lock($LogQueue);
        $log_arr_ref = [];
        cond_signal($LogQueue);
    }

    if($Debug) {
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
    lock($LogQueue);
    cond_signal($LogQueue);
}

$MainThreadOver=1;

print "waiting for worker finish\n";
$thr->join();


#output the result
print "logfile=$LogFile\n";
print "totalrecord=$totalrecord\n";
print "status500cnt=$status500cnt\n";
for my $key (keys %costmap) {
    print "$key=$costmap{$key}\n";
}

