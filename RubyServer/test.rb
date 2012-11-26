#/usr/bin/env ruby

require 'sqlite3'
require 'json'
require 'socket'

webserver = TCPServer.new('138.67.77.103', 5050)

# Determines what to do with the request and calls the corresponding function
def handle_requests(request)
	puts request
	data = JSON.parse(request)
	if data['command'] == 'send_message'
		return send_message(data['number'], data['data'])
	elsif data['command'] == 'play_tone'
		return play_tone(data['number'], data['data'])
	else
		return "ERROR"
	end
end

# Determines where to send the message and sends it
def send_message(number, message)	
	ip_addr = get_ip_address(number)
	
	# Create the dictionary with the necessary information
	data = Hash.new 
	data['command'] = 'display_message'
	data['data'] = 'message'

	# Send the message and other information to the phone
	send_to_phone(ip_addr, data.to_json)

	puts "SENDING MESSAGE: #{message} TO: #{ip_addr}"
	return "DONE"
end

# Sends the command to play a tone on the phone
def play_tone(number, tone)
	puts "PLAYING TONE: #{tone}"
	return "DONE"
end

# Get the ip address of the phone from the database
def get_ip_address(phone_number)
	db = SQLite3::Database.open("phones.sqlite")
	ip_addr = db.execute("SELECT ip_address FROM phone WHERE phone_number = ?", phone_number)[0][0]
	db.close
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
