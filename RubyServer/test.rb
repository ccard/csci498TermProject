#/usr/bin/env ruby

require 'sqlite3'
require 'json'
require 'socket'

webserver = TCPServer.new('138.67.77.103', 5050)

# JSON: number, name, command, data
# {"data":"Trix","command":"send_message","number":32777,"name":"whats up"}
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

def send_message(number, message)
	db = SQLite3::Database.open("phones.sqlite")
	ip_addr = db.execute("SELECT ip_address FROM phone WHERE phone_number = ?", number)[0][0]
	puts ip_addr
	db.close
	s = TCPSocket.open(ip_addr, 5050)
	s.puts message
	return "SENDING MESSAGE: #{message}"
end

def play_tone(number, tone)
	return "PLAYING TONE"
end

while session = webserver.accept
	# session.puts "HELLO"
	request = session.gets
	session.print handle_requests(request)
	session.close
end
