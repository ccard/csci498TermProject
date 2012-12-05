#!/usr/bin/perl

# use warnings;
use strict;

my @lines = <>;
my $statement = "";
my $table_name = "INITIAL";
my @SQL;

foreach (@lines) {
	next if /^\s+$/;
	chomp;
	# print "line is $_ \n";
	if (/:/) {
		if ($statement) {
			$statement .= ");";
			$statement =~ s/ ,/,/g;
			$statement =~ s/, \)/\)/;
			push @SQL, $statement;
		}
		($table_name) = /(\w+):/;
		# print "Table name is $table_name \n";
		$statement = "CREATE TABLE ${table_name}(";
	} else {
		s/\s+/ /g;
		$statement .= $_;
		$statement .= ", ";
	}
}

foreach (@SQL) {
	print "$_ \n";
}

print "INSERT INTO phone_type(name) VALUES ('Smart Phone');";