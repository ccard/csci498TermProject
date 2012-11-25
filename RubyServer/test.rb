#/usr/bin/env ruby

#require "sqlite3"
require 'json'
require 'socket'

# JSON: number, name, command, data
def handle_requests(request)
	puts request
	data = JSON.parse(request)
	if data['command'] == 'send_message'
		return send_message(data['data'])
	else
		return "ERROR"
	end
end

def send_message(message)
	
end



webserver = TCPServer.new 5050

# while line = webserver.gets
# 	puts line	
# end


while session = webserver.accept
	# session.puts "HELLO"
	puts "HELLO"
	request = session.gets
	session.print handle_requests(request)
	session.close
end

