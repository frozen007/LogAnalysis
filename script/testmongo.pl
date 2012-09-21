use MongoDB;
my $conn;
my $db;
my $users;
eval{
$conn = MongoDB::Connection->new(host=>'localhost', port=>27017);
$db = $conn->tutorial;
$users = $db->users;
};
my $data = {"name"=>"Joe", "age"=>52, "likes"=>[qw/skiing math ponies/]};
if($users) {
    $users->insert($data);
} else {
    print "no mongo found\n";
}
