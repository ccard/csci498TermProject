#/usr/bin/env ruby

require 'sqlite3'
require 'json'
require 'socket'

$db = SQLite3::Database.open("phones.sqlite")
webserver = TCPServer.new('138.67.77.103', 5050)
UPDATE_LOCATION_STATEMENT = "UPDATE phone SET last_lattitude=?, last_longitude=? WHERE id_unique=?"
USER_EXISTS_STATEMENT = "SELECT COUNT(*) FROM user WHERE email = ?"
PHONE_EXISTS_STATEMENT = "SELECT COUNT(*) FROM phone WHERE id_unique = ?"
GET_USER_ID_STATEMENT = "SELECT id FROM user WHERE email=?"
GET_USER_ID_AUTH_STATEMENT = "SELECT id FROM user WHERE email=? AND password_hash=?"
GET_PHONE_TYPE_ID_STATEMENT = "SELECT id FROM phone_type WHERE name=?"
ADD_PHONE_STATEMENT = "INSERT INTO phone(name, ip_address, last_lattitude, last_longitude, id_unique, user_id, phone_type_id) VALUES (?, ?, ?, ?, ?, ?, ?)"
REMOVE_PHONE_STATEMENT = "DELETE FROM phone WHERE id_unique=?"
ADD_USER_STATEMENT = "INSERT INTO user(email, password_hash) VALUES (?, ?)"
GET_ALL_PHONES = "SELECT * FROM phone, phone_type WHERE phone.phone_type_id = phone_type.id AND user_id=?"
COLUMN_NAMES = ['id', 'name', 'ip_address', 'last_lattitude', 'last_longitude', 'id_unique', 'user_id', 'phone_type_id', 'phone_type_id', 'phone_type']

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
	elsif data['command'] == 'update_location'
		return update_location(data)
	else
		puts "Unknown command: #{data['command']}"
		return "ERROR"
	end
end

def unique_phone?(id_unique)
	phone_id_count = $db.execute(PHONE_EXISTS_STATEMENT, id_unique)[0][0]
	if Integer(phone_id_count) == 0
		return true		
	else
		return false
	end
end

def update_location(data)
	$db.execute(UPDATE_LOCATION_STATEMENT, [data['last_lattitude'], data['last_longitude'], data['id_unique']])
	return "DONE"
end

def add_phone(data)
	user_id = $db.execute(GET_USER_ID_STATEMENT, data['email'])[0][0]
	phone_type_id = $db.execute(GET_PHONE_TYPE_ID_STATEMENT, data['phone_type'])[0][0]
	if unique_phone?(data['id_unique'])
		$db.execute(ADD_PHONE_STATEMENT, [data['name'], data['ip_address'], data['last_lattitude'], data['last_longitude'], data['id_unique'], user_id, phone_type_id])
	else
		return "PHONE_ADD_ERROR"
	end
	return "DONE"
end

def remove_phone(data)
	$db.execute(REMOVE_PHONE_STATEMENT, data['id_unique'])	
	return "DONE"
end

def create_account(data)
	user_exists = $db.execute(USER_EXISTS_STATEMENT, data['email'])[0][0]
	if Integer(user_exists) != 0
		puts user_exists
		return "USER_EXISTS_ERROR"
	end
	if !unique_phone?(data['id_unique'])
		return "PHONE_ADD_ERROR"
	end
	$db.execute(ADD_USER_STATEMENT, [data['email'], data['password_hash']])
	return add_phone(data)
end

def login(data)
	user_exists = $db.execute(USER_EXISTS_STATEMENT, data['email'])[0][0]
	if Integer(user_exists) != 0
		result = $db.execute(GET_USER_ID_AUTH_STATEMENT, [data['email'], data['password_hash']])
		begin
			user_id = result[0][0]
			phones = $db.execute(GET_ALL_PHONES, user_id)
			phones_hash = Hash.new
			phones.each do |phone|
				phone_hash = Hash.new
				for i in 0..(phone.length - 1)
					phone_hash[COLUMN_NAMES[i]] = phone[i]
				end
				phones_hash[phone[1]] = phone_hash
			end
			return phones_hash.to_json
		rescue Exception => e
			return "ERROR"
		end
	else
		return "ERROR"
	end
end

# Get the ip address of the phone from the database
def get_ip_address(id_unique)
	ip_addr = $db.execute("SELECT ip_address FROM phone WHERE id_unique = ?", id_unique)[0][0]
	return ip_addr	
end

# Open a socket to the phone and send the data
def send_to_phone(ip_addr, data)
	s = TCPSocket.open(ip_addr, 5050)
	s.puts data
	s.close
end

begin
	while session = webserver.accept
		request = session.gets
		session.print handle_requests(request)
		session.close
		puts "Ready for next request"
	end
rescue Exception => e
	puts "Closing Server: #{e}"
	webserver.close
	$db.close
end
