#/usr/bin/env ruby

require 'sqlite3'
require 'json'
require 'socket'

# JSON: number, name, command, data
def handle_requests(request)
	puts request
	data = JSON.parse(request)
	if data['command'] == 'send_message'
		return send_message(data['number'], data['data'])
	elsif data['command'] == 'play_tone'
		return play_tone(data['number'], data['data'])
	elsif
		return "ERROR"
	end
end

def send_message(number, message)
	return "SENDING MESSAGE: #{message}"
end

def play_tone(number, tone)
	return "PLAYING TONE"
end


webserver = TCPServer.new('138.67.77.103', 5050)

while session = webserver.accept
	# session.puts "HELLO"
	request = session.gets
	session.print handle_requests(request)
	session.close
end

