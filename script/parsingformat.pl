# Copied from AWStats by zhaomingyu
# version: awstats-7.0
#
sub DefinePerlParsingFormat {
	my ($LogFormat, $LogSeparator) = @_;
	$pos_vh = $pos_host = $pos_logname = $pos_date = $pos_tz = $pos_method =
	  $pos_url = $pos_code = $pos_size = -1;

    #cost
    $pos_cost = -1;

	$pos_referer = $pos_agent = $pos_query = $pos_gzipin = $pos_gzipout =
	  $pos_compratio   = -1;
	$pos_cluster       = $pos_emails = $pos_emailr = $pos_hostr = -1;
	@pos_extra         = ();
	@fieldlib          = ();
	$PerlParsingFormat = '';
    if($LogSeparator eq '') {
        $LogSeparator = " ";
    }

# Log records examples:
# Apache combined:             62.161.78.73 user - [dd/mmm/yyyy:hh:mm:ss +0000] "GET / HTTP/1.1" 200 1234 "http://www.from.com/from.htm" "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)"
# Apache combined (408 error): my.domain.com - user [09/Jan/2001:11:38:51 -0600] "OPTIONS /mime-tmp/xxx file.doc HTTP/1.1" 408 - "-" "-"
# Apache combined (408 error): 62.161.78.73 user - [dd/mmm/yyyy:hh:mm:ss +0000] "-" 408 - "-" "-"
# Apache combined (400 error): 80.8.55.11 - - [28/Apr/2007:03:20:02 +0200] "GET /" 400 584 "-" "-"
# IIS:                         2000-07-19 14:14:14 62.161.78.73 - GET / 200 1234 HTTP/1.1 Mozilla/4.0+(compatible;+MSIE+5.01;+Windows+NT+5.0) http://www.from.com/from.htm
# WebStar:                     05/21/00	00:17:31	OK  	200	212.242.30.6	Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)	http://www.cover.dk/	"www.cover.dk"	:Documentation:graphics:starninelogo.white.gif	1133
# Squid extended:              12.229.91.170 - - [27/Jun/2002:03:30:50 -0700] "GET http://www.callistocms.com/images/printable.gif HTTP/1.1" 304 354 "-" "Mozilla/5.0 Galeon/1.0.3 (X11; Linux i686; U;) Gecko/0" TCP_REFRESH_HIT:DIRECT
# Log formats:
# Apache common_with_mod_gzip_info1: %h %l %u %t \"%r\" %>s %b mod_gzip: %{mod_gzip_compression_ratio}npct.
# Apache common_with_mod_gzip_info2: %h %l %u %t \"%r\" %>s %b mod_gzip: %{mod_gzip_result}n In:%{mod_gzip_input_size}n Out:%{mod_gzip_output_size}n:%{mod_gzip_compression_ratio}npct.
# Apache deflate: %h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\" (%{ratio}n)
	if ($Debug) {
		debug(
"Call To DefinePerlParsingFormat (LogType='$LogType', LogFormat='$LogFormat')"
		);
	}
	if ( $LogFormat =~ /^[1-6]$/ ) {    # Pre-defined log format
		if ( $LogFormat eq '1' || $LogFormat eq '6' )
		{ # Same than "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-Agent}i\"".
			 # %u (user) is "([^\\/\\[]+)" instead of "[^ ]+" because can contain space (Lotus Notes). referer and ua might be "".

# $PerlParsingFormat="([^ ]+) [^ ]+ ([^\\/\\[]+) \\[([^ ]+) [^ ]+\\] \\\"([^ ]+) (.+) [^\\\"]+\\\" ([\\d|-]+) ([\\d|-]+) \\\"(.*?)\\\" \\\"([^\\\"]*)\\\"";
			$PerlParsingFormat =
"([^ ]+) [^ ]+ ([^\\/\\[]+) \\[([^ ]+) [^ ]+\\] \\\"([^ ]+) ([^ ]+)(?: [^\\\"]+|)\\\" ([\\d|-]+) ([\\d|-]+) \\\"(.*?)\\\" \\\"([^\\\"]*)\\\"";
			$pos_host    = 0;
			$pos_logname = 1;
			$pos_date    = 2;
			$pos_method  = 3;
			$pos_url     = 4;
			$pos_code    = 5;
			$pos_size    = 6;
			$pos_referer = 7;
			$pos_agent   = 8;
			@fieldlib    = (
				'host', 'logname', 'date', 'method', 'url', 'code',
				'size', 'referer', 'ua'
			);
		}
		elsif ( $LogFormat eq '2' )
		{ # Same than "date time c-ip cs-username cs-method cs-uri-stem sc-status sc-bytes cs-version cs(User-Agent) cs(Referer)"
			$PerlParsingFormat =
"(\\S+ \\S+) (\\S+) (\\S+) (\\S+) (\\S+) ([\\d|-]+) ([\\d|-]+) \\S+ (\\S+) (\\S+)";
			$pos_date    = 0;
			$pos_host    = 1;
			$pos_logname = 2;
			$pos_method  = 3;
			$pos_url     = 4;
			$pos_code    = 5;
			$pos_size    = 6;
			$pos_agent   = 7;
			$pos_referer = 8;
			@fieldlib    = (
				'date', 'host', 'logname', 'method', 'url', 'code',
				'size', 'ua',   'referer'
			);
		}
		elsif ( $LogFormat eq '3' ) {
			$PerlParsingFormat =
"([^\\t]*\\t[^\\t]*)\\t([^\\t]*)\\t([\\d|-]*)\\t([^\\t]*)\\t([^\\t]*)\\t([^\\t]*)\\t[^\\t]*\\t([^\\t]*)\\t([\\d]*)";
			$pos_date    = 0;
			$pos_method  = 1;
			$pos_code    = 2;
			$pos_host    = 3;
			$pos_agent   = 4;
			$pos_referer = 5;
			$pos_url     = 6;
			$pos_size    = 7;
			@fieldlib    = (
				'date', 'method',  'code', 'host',
				'ua',   'referer', 'url',  'size'
			);
		}
		elsif ( $LogFormat eq '4' ) {    # Same than "%h %l %u %t \"%r\" %>s %b"
			 # %u (user) is "(.+)" instead of "[^ ]+" because can contain space (Lotus Notes).
			$PerlParsingFormat =
"([^ ]+) [^ ]+ (.+) \\[([^ ]+) [^ ]+\\] \\\"([^ ]+) ([^ ]+)(?: [^\\\"]+|)\\\" ([\\d|-]+) ([\\d|-]+)";
			$pos_host    = 0;
			$pos_logname = 1;
			$pos_date    = 2;
			$pos_method  = 3;
			$pos_url     = 4;
			$pos_code    = 5;
			$pos_size    = 6;
			@fieldlib    =
			  ( 'host', 'logname', 'date', 'method', 'url', 'code', 'size' );
		}
	}
	else {    # Personalized log format
		my $LogFormatString = $LogFormat;

		# Replacement for Notes format string that are not Apache
		$LogFormatString =~ s/%vh/%virtualname/g;

		# Replacement for Apache format string
		$LogFormatString =~ s/%v(\s)/%virtualname$1/g;
		$LogFormatString =~ s/%v$/%virtualname/g;
		$LogFormatString =~ s/%h(\s)/%host$1/g;
		$LogFormatString =~ s/%h$/%host/g;
		$LogFormatString =~ s/%l(\s)/%other$1/g;
		$LogFormatString =~ s/%l$/%other/g;
		$LogFormatString =~ s/\"%u\"/%lognamequot/g;
		$LogFormatString =~ s/%u(\s)/%logname$1/g;
		$LogFormatString =~ s/%u$/%logname/g;
		$LogFormatString =~ s/%t(\s)/%time1$1/g;
		$LogFormatString =~ s/%t$/%time1/g;
		$LogFormatString =~ s/\"%r\"/%methodurl/g;
		$LogFormatString =~ s/%>s/%code/g;
		$LogFormatString =~ s/%b(\s)/%bytesd$1/g;
		$LogFormatString =~ s/%b$/%bytesd/g;
		$LogFormatString =~ s/\"%{Referer}i\"/%refererquot/g;
		$LogFormatString =~ s/\"%{User-Agent}i\"/%uaquot/g;
		$LogFormatString =~ s/%{mod_gzip_input_size}n/%gzipin/g;
		$LogFormatString =~ s/%{mod_gzip_output_size}n/%gzipout/g;
		$LogFormatString =~ s/%{mod_gzip_compression_ratio}n/%gzipratio/g;
		$LogFormatString =~ s/\(%{ratio}n\)/%deflateratio/g;

		# Replacement for a IIS and ISA format string
		$LogFormatString =~ s/cs-uri-query/%query/g;    # Must be before cs-uri
		$LogFormatString =~ s/date\stime/%time2/g;
		$LogFormatString =~ s/c-ip/%host/g;
		$LogFormatString =~ s/cs-username/%logname/g;
		$LogFormatString =~ s/cs-method/%method/g;  # GET, POST, SMTP, RETR STOR
		$LogFormatString =~ s/cs-uri-stem/%url/g;
		$LogFormatString =~ s/cs-uri/%url/g;
		$LogFormatString =~ s/sc-status/%code/g;
		$LogFormatString =~ s/sc-bytes/%bytesd/g;
		$LogFormatString =~ s/cs-version/%other/g;  # Protocol
		$LogFormatString =~ s/cs\(User-Agent\)/%ua/g;
		$LogFormatString =~ s/c-agent/%ua/g;
		$LogFormatString =~ s/cs\(Referer\)/%referer/g;
		$LogFormatString =~ s/cs-referred/%referer/g;
		$LogFormatString =~ s/sc-authenticated/%other/g;
		$LogFormatString =~ s/s-svcname/%other/g;
		$LogFormatString =~ s/s-computername/%other/g;
		$LogFormatString =~ s/r-host/%virtualname/g;
		$LogFormatString =~ s/cs-host/%virtualname/g;
		$LogFormatString =~ s/r-ip/%other/g;
		$LogFormatString =~ s/r-port/%other/g;
		$LogFormatString =~ s/time-taken/%other/g;
		$LogFormatString =~ s/cs-bytes/%other/g;
		$LogFormatString =~ s/cs-protocol/%other/g;
		$LogFormatString =~ s/cs-transport/%other/g;
		$LogFormatString =~
		  s/s-operation/%method/g;    # GET, POST, SMTP, RETR STOR
		$LogFormatString =~ s/cs-mime-type/%other/g;
		$LogFormatString =~ s/s-object-source/%other/g;
		$LogFormatString =~ s/s-cache-info/%other/g;
		$LogFormatString =~ s/cluster-node/%cluster/g;
		$LogFormatString =~ s/s-sitename/%other/g;
		$LogFormatString =~ s/s-ip/%other/g;
		$LogFormatString =~ s/s-port/%other/g;
		$LogFormatString =~ s/cs\(Cookie\)/%other/g;
		$LogFormatString =~ s/sc-substatus/%other/g;
		$LogFormatString =~ s/sc-win32-status/%other/g;


		# Added for MMS
		$LogFormatString =~
		  s/protocol/%protocolmms/g;    # cs-method might not be available
		$LogFormatString =~
		  s/c-status/%codemms/g;    # c-status used when sc-status not available
		if ($Debug) { debug(" LogFormatString=$LogFormatString"); }

# $LogFormatString has an AWStats format, so we can generate PerlParsingFormat variable
		my $i                       = 0;
		my $LogSeparatorWithoutStar = $LogSeparator;
		$LogSeparatorWithoutStar =~ s/[\*\+]//g;
		foreach my $f ( split( /\s+/, $LogFormatString ) ) {
            if($Debug) {
                print "format: $f\n";
            }

			# Add separator for next field
			if ($PerlParsingFormat) { $PerlParsingFormat .= "$LogSeparator"; }

			# Special for logname
			if ( $f =~ /%lognamequot$/ ) {
				$pos_logname = $i;
				$i++;
				push @fieldlib, 'logname';
				$PerlParsingFormat .=
				  "\\\"?([^\\\"]*)\\\"?"
				  ; # logname can be "value", "" and - in same log (Lotus notes)
			}
			elsif ( $f =~ /%logname$/ ) {
				$pos_logname = $i;
				$i++;
				push @fieldlib, 'logname';

# %u (user) is "([^\\/\\[]+)" instead of "[^$LogSeparatorWithoutStar]+" because can contain space (Lotus Notes).
				$PerlParsingFormat .= "([^\\/\\[]+)";
			}

			# Date format
			elsif ( $f =~ /%time1$/ || $f =~ /%time1b$/ )
			{ # [dd/mmm/yyyy:hh:mm:ss +0000] or [dd/mmm/yyyy:hh:mm:ss],  time1b kept for backward compatibility
				$pos_date = $i;
				$i++;
				push @fieldlib, 'date';
				$pos_tz = $i;
				$i++;
				push @fieldlib, 'tz';
				$PerlParsingFormat .=
"\\[([^$LogSeparatorWithoutStar]+)( [^$LogSeparatorWithoutStar]+)?\\]";
			}
			elsif ( $f =~ /%time2$/ ) {    # yyyy-mm-dd hh:mm:ss
				$pos_date = $i;
				$i++;
				push @fieldlib, 'date';
				$PerlParsingFormat .=
"([^$LogSeparatorWithoutStar]+\\s[^$LogSeparatorWithoutStar]+)"
				  ;                        # Need \s for Exchange log files
			}
			elsif ( $f =~ /%time3$/ )
			{ # mon d hh:mm:ss  or  mon  d hh:mm:ss  or  mon dd hh:mm:ss yyyy  or  day mon dd hh:mm:ss  or  day mon dd hh:mm:ss yyyy
				$pos_date = $i;
				$i++;
				push @fieldlib, 'date';
				$PerlParsingFormat .=
"(?:\\w\\w\\w )?(\\w\\w\\w \\s?\\d+ \\d\\d:\\d\\d:\\d\\d(?: \\d\\d\\d\\d)?)";
			}
			elsif ( $f =~ /%time4$/ ) {    # ddddddddddddd
				$pos_date = $i;
				$i++;
				push @fieldlib, 'date';
				$PerlParsingFormat .= "(\\d+)";
			}

			# Special for methodurl and methodurlnoprot
			elsif ( $f =~ /%methodurl$/ ) {
				$pos_method = $i;
				$i++;
				push @fieldlib, 'method';
				$pos_url = $i;
				$i++;
				push @fieldlib, 'url';
				$PerlParsingFormat .=

#"\\\"([^$LogSeparatorWithoutStar]+) ([^$LogSeparatorWithoutStar]+) [^\\\"]+\\\"";
"\\\"([^$LogSeparatorWithoutStar]+) ([^$LogSeparatorWithoutStar]+)(?: [^\\\"]+|)\\\"";
			}
			elsif ( $f =~ /%methodurlnoprot$/ ) {
				$pos_method = $i;
				$i++;
				push @fieldlib, 'method';
				$pos_url = $i;
				$i++;
				push @fieldlib, 'url';
				$PerlParsingFormat .=
"\\\"([^$LogSeparatorWithoutStar]+) ([^$LogSeparatorWithoutStar]+)\\\"";
			}

			# Common command tags
			elsif ( $f =~ /%virtualnamequot$/ ) {
				$pos_vh = $i;
				$i++;
				push @fieldlib, 'vhost';
				$PerlParsingFormat .= "\\\"([^$LogSeparatorWithoutStar]+)\\\"";
			}
			elsif ( $f =~ /%virtualname$/ ) {
				$pos_vh = $i;
				$i++;
				push @fieldlib, 'vhost';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%host_r$/ ) {
				$pos_hostr = $i;
				$i++;
				push @fieldlib, 'hostr';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%host$/ ) {
				$pos_host = $i;
				$i++;
				push @fieldlib, 'host';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%host_proxy$/ )
			{    # if host_proxy tag used, host tag must not be used
				$pos_host = $i;
				$i++;
				push @fieldlib, 'host';
				$PerlParsingFormat .= "(.+?)(?:, .*)*";
			}
			elsif ( $f =~ /%method$/ ) {
				$pos_method = $i;
				$i++;
				push @fieldlib, 'method';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%url$/ ) {
				$pos_url = $i;
				$i++;
				push @fieldlib, 'url';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%query$/ ) {
				$pos_query = $i;
				$i++;
				push @fieldlib, 'query';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%code$/ ) {
				$pos_code = $i;
				$i++;
				push @fieldlib, 'code';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
            #added by zhaomingyu begin
			elsif ( $f =~ /%cost$/ ) {
				$pos_cost = $i;
				$i++;
				push @fieldlib, 'cost';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%costquot$/ ) {
				$pos_cost = $i;
				$i++;
				push @fieldlib, 'cost';
				$PerlParsingFormat .= "\\\"([^$LogSeparatorWithoutStar]+)\\\"";
			}
            #by zhaomingyu end
			elsif ( $f =~ /%bytesd$/ ) {
				$pos_size = $i;
				$i++;
				push @fieldlib, 'size';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%refererquot$/ ) {
				$pos_referer = $i;
				$i++;
				push @fieldlib, 'referer';
				$PerlParsingFormat .=
				  "\\\"([^\\\"]*)\\\"";    # referer might be ""
			}
			elsif ( $f =~ /%referer$/ ) {
				$pos_referer = $i;
				$i++;
				push @fieldlib, 'referer';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%uaquot$/ ) {
				$pos_agent = $i;
				$i++;
				push @fieldlib, 'ua';
				$PerlParsingFormat .= "\\\"([^\\\"]*)\\\"";    # ua might be ""
			}
			elsif ( $f =~ /%uabracket$/ ) {
				$pos_agent = $i;
				$i++;
				push @fieldlib, 'ua';
				$PerlParsingFormat .= "\\\[([^\\\]]*)\\\]";    # ua might be []
			}
			elsif ( $f =~ /%ua$/ ) {
				$pos_agent = $i;
				$i++;
				push @fieldlib, 'ua';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%gzipin$/ ) {
				$pos_gzipin = $i;
				$i++;
				push @fieldlib, 'gzipin';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%gzipout/ )
			{ # Compare $f to /%gzipout/ and not to /%gzipout$/ like other fields
				$pos_gzipout = $i;
				$i++;
				push @fieldlib, 'gzipout';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%gzipratio/ )
			{ # Compare $f to /%gzipratio/ and not to /%gzipratio$/ like other fields
				$pos_compratio = $i;
				$i++;
				push @fieldlib, 'gzipratio';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%deflateratio/ )
			{ # Compare $f to /%deflateratio/ and not to /%deflateratio$/ like other fields
				$pos_compratio = $i;
				$i++;
				push @fieldlib, 'deflateratio';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%email_r$/ ) {
				$pos_emailr = $i;
				$i++;
				push @fieldlib, 'email_r';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%email$/ ) {
				$pos_emails = $i;
				$i++;
				push @fieldlib, 'email';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%cluster$/ ) {
				$pos_cluster = $i;
				$i++;
				push @fieldlib, 'clusternb';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}
			elsif ( $f =~ /%timetaken$/ ) {
				$pos_timetaken = $i;
				$i++;
				push @fieldlib, 'timetaken';
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}

# Special for protocolmms, used for method if method not already found (for MMS)
			elsif ( $f =~ /%protocolmms$/ ) {
				if ( $pos_method < 0 ) {
					$pos_method = $i;
					$i++;
					push @fieldlib, 'method';
					$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
				}
			}

   # Special for codemms, used for code only if code not already found (for MMS)
			elsif ( $f =~ /%codemms$/ ) {
				if ( $pos_code < 0 ) {
					$pos_code = $i;
					$i++;
					push @fieldlib, 'code';
					$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
				}
			}

			# Extra tag
			elsif ( $f =~ /%extra(\d+)$/ ) {
				$pos_extra[$1] = $i;
				$i++;
				push @fieldlib, "extra$1";
				$PerlParsingFormat .= "([^$LogSeparatorWithoutStar]+)";
			}

			# Other tag
			elsif ( $f =~ /%other$/ ) {
				$PerlParsingFormat .= "[^$LogSeparatorWithoutStar]+";
			}
			elsif ( $f =~ /^%otherquot$/ ) {
                $PerlParsingFormat .= "\\\"[^\\\"]*\\\"";
			}

            #added by zhaomingyu for the double quot problem without seperator
            elsif ( $f =~ /^%otherquot%otherquot$/ ) {
                $PerlParsingFormat .= "\\\"[^\\\"]*\\\"\\\"[^\\\"]*\\\"";
            }
            #added by zhaomingyu end

			# Unknown tag (no parenthesis)
			else {
				$PerlParsingFormat .= "[^$LogSeparatorWithoutStar]+";
			}
		}
		if ( !$PerlParsingFormat ) {
			error("No recognized format tag in personalized LogFormat string");
		}
	}

	$PerlParsingFormat = qr/^$PerlParsingFormat/;
    if ($Debug) { debug(" PerlParsingFormat is $PerlParsingFormat"); }
}

sub error{
    my $message = shift;
    print $message;
}

sub debug{
    my $message = shift;
    print $message;
}

1;