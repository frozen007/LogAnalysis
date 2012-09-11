my $ErrLogFile=shift;

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
print "logfile=$ErrLogFile\n";
print "exceptioncnt=$exceptioncnt\n";