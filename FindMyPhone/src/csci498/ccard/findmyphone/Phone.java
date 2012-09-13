/*
 * Chris Card
 * 9/7/12
 */
package csci498.ccard.findmyphone;

public class Phone {

	private int number;
	private String name;
	private String phonetype;
	
	
	public Phone() {
		super();
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhonetype() {
		return phonetype;
	}
	public void setPhonetype(String phonetype) {
		this.phonetype = phonetype;
	}
	
	/**
	 * This method sends a message to the selected phone through the servier
	 * and the phone communication classes
	 * @param message the message to send
	 * @return true if the message was able to be sent false other wise
	 */
	public boolean sendMessage(String message)
	{
		//phone comminication here
		return true;
	}
	
	/**
	 * this method sends the instruction to play a tone on the selected device
	 * @return true if success false other wise
	 */
	public boolean sendTone()
	{
		//phone communication here
		return true;
	}
	
	public String toString()
	{
		return name;
	}
}
