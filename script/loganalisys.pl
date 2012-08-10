require ("parsingformat.pl");

my $LogFile=shift;
my $LogFormat=shift;
my $LogSeparator=shift;
my $LogCostUnit=shift;
my $ErrLogFile=shift;

#$LogFile="D:/mydoc/work/loganalisys/log/resinlog_spr/64_128/access.log.cost";
#$ErrLogFile="D:/mydoc/work/loganalisys/log/resinlog_spr/64_128/stderr.log";
$Debug=1;

#$LogFormat="%host %other %other %time1 %methodurl %code %bytesd %refererquot %otherquot %otherquot %otherquot %otherquot %otherquot %otherquot %costquot";
#$LogCostUnit="s";

if($LogFormat eq '') {
    $LogFormat="%host %other %other %time1 %methodurl %code %cost %bytesd %otherquot %otherquot";
}
if($LogSeparator eq '') {
    $LogSeparator=" ";
}
if($LogCostUnit eq '') {
    $LogCostUnit="us";
}

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
#####LogCostUnit#####
# us=microsecond
# ms=millisecond
# s=second
#####################
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

    $cost = $fieldList[$pos_cost];

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

my $exceptioncnt=0;
if( -e $ErrLogFile) {
    open(ERRLOG, $ErrLogFile);
    binmode ERRLOG;
    while(<ERRLOG>) {
        my $line = $_;
        chomp $line;
        if($line eq '') {
            next;
        }

        if( $line =~ /^\s+at/) {
            next;
        }

        if( $line =~ /^([0-9a-zA-Z]+\.)+[0-9a-zA-Z]*(Exception|Error)/ ) {
            $exceptioncnt++;
        }
    }
}



#output the result
print "logfile=$LogFile\n";
print "totalrecord=$totalrecord\n";
print "status500cnt=$status500cnt\n";
print "exceptioncnt=$exceptioncnt\n";
for my $key (keys %costmap) {
    print "$key=$costmap{$key}\n";
}

