require ("parsingformat.pl");

my $LogFile=shift;
my $LogFormat=shift;
my $LogSeparator=shift;
my $LogCostUnit=shift;
my $ErrLogFile=shift;

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

    $cost = $fieldList[$pos_cost];

    print "line:$totalrecord", ", cost:$cost\n"; 

	if($totalrecord > 20) {
        last;
	}
        


}