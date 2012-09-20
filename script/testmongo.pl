require("HttpRequestLog.pm");
use MongoDB;
my $conn = MongoDB::Connection->new(host=>'localhost', port=>27017);
my $db = $conn->tutorial;
=head

my $users = $db->users;
my $data = {"name"=>"Joe", "age"=>52, "likes"=>[qw/skiing math ponies/]};
$users->insert($data);
=cut

my $httplog = HttpRequestLog->new(url=>'hehe.act', cost=>1000);
my $logs = $db->get_collection('httprequest');
#my $logs = $db->httprequest->log;
$logs->insert($httplog->get_data());

