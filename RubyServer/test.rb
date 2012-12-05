#/usr/bin/env ruby

require 'sqlite3'
require 'json'
require 'socket'

$db = SQLite3::Database.open("phones.sqlite")
webserver = TCPServer.new('138.67.77.103', 5050)
USER_EXISTS_STATEMENT = "SELECT COUNT(*) FROM user WHERE email = ?"
GET_USER_ID_STATEMENT = "SELECT id FROM user WHERE email=? AND password_hash=?"
GET_PHONE_TYPE_ID_STATEMENT = "SELECT id FROM phone_type WHERE name=?"
ADD_PHONE_STATEMENT = "INSERT INTO phone(name, ip_address, last_lattitude, last_longitude, id_unique, user_id, phone_type_id) VALUES (?, ?, ?, ?, ?, ?, ?)"
REMOVE_PHONE_STATEMENT = "DELETE FROM phone WHERE id_unique=?"
ADD_USER_STATEMENT = "INSERT INTO user(email, password_hash) VALUES (?, ?)"
GET_ALL_PHONES = "SELECT * FROM phone WHERE user_id=?"
COLUMN_NAMES = ['id', 'name', 'ip_address', 'last_lattitude', 'last_longitude', 'id_unique', 'user_id', 'phone_type_id']

# Determines what to do with the request and calls the corresponding function
def handle_requests(request)
	puts request
	data = JSON.parse(request)
	if data['command'] == 'login'
		return login(data)
	elsif data['command'] == 'add_phone'
		return add_phone(data)
	elsif data['command'] == 'remove_phone'
		return remove_phone(data)			
	elsif data['command'] == 'create_account'
		return create_account(data)
	else
		return "ERROR"
	end
end

def add_phone(data)
	# $db = SQLite3::Database.open("phones.sqlite")
	user_id = $db.execute(GET_USER_ID_STATEMENT, data['email'], data['password_hash'])[0][0]
	phone_type_id = $db.execute(GET_PHONE_TYPE_ID_STATEMENT, data['phone_type'])[0][0]
	$db.execute(ADD_PHONE_STATEMENT, data['name'], data['ip_address'], data['last_lattitude'], data['last_longitude'], data['id_unique'], user_id, phone_type_id)
	# $db.close
	return "DONE"
end

def remove_phone(data)
	# $db = SQLite3::Database.open("phones.sqlite")
	$db.execute(REMOVE_PHONE_STATEMENT, data['id_unique'])	
	# $db.close
	return "DONE"
end

def create_account(data)
	# $db = SQLite3::Database.open("phones.sqlite")
	user_exists = $db.execute(USER_EXISTS_STATEMENT, data['email'])[0][0]
	if Integer(user_exists) != 0
		puts user_exists
		return "ERROR"
	end
	$db.execute(ADD_USER_STATEMENT, data['email'], data['password_hash'])
	# $db.close
	# return "DONE"
	return add_phone(data)
end

def login(data)
	# $db = SQLite3::Database.open("phones.sqlite")
	user_id = $db.execute(GET_USER_ID_STATEMENT, data['email'], data['password_hash'])[0][0]
	if user_id
		phones = $db.execute(GET_ALL_PHONES, user_id)
		# $db.close
		phones_hash = Hash.new
		phones.each do |phone|
			phone_hash = Hash.new
			for i in 0..(phone.length - 1)
				phone_hash[COLUMN_NAMES[i]] = phone[i]
			end
			phones_hash[phone[1]] = phone_hash
		end
		return phones_hash.to_json
	else
		# $db.close
		return "ERROR"
	end
end

# Get the ip address of the phone from the database
def get_ip_address(id_unique)
	# $db = SQLite3::Database.open("phones.sqlite")
	ip_addr = $db.execute("SELECT ip_address FROM phone WHERE id_unique = ?", id_unique)[0][0]
	# $db.close
	return ip_addr	
end

# Open a socket to the phone and send the data
def send_to_phone(ip_addr, data)
	s = TCPSocket.open(ip_addr, 5050)
	s.puts data
	s.close
end

while session = webserver.accept
	# session.puts "HELLO"
	request = session.gets
	session.print handle_requests(request)
	session.close
end
$db.close