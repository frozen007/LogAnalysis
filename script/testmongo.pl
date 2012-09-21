use MongoDB;
my $conn = MongoDB::Connection->new(host=>'localhost', port=>27017);
my $db = $conn->tutorial;

my $users = $db->users;
my $data = {"name"=>"Joe", "age"=>52, "likes"=>[qw/skiing math ponies/]};
$users->insert($data);
