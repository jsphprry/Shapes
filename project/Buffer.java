import java.lang.StringBuffer;
import java.util.Stack;
public class Buffer extends Display{
	// declare and initialise the stacks needed for the history function
	private Stack<String> backHistory = new Stack<>();
	private Stack<String> forwardHistory = new Stack<>();
	/** <p>Initialise buffer</p>
	 * @param y height of buffer
	 * @param x width of buffer
	 * */
	public Buffer(int y, int x){
		// initialise the buffer with dimensions provided as y x
		buffer = new char[y][x];
	}
	/** <p>Record the buffer to history</p>
	 * */
	public void recordBuffer(){
		// create stringbuffer object
		StringBuffer bufferString = new StringBuffer("");
		// for every row in buffer
		for (int i=0; i < buffer.length; i++){
			// for every column in row in buffer
			for (int j=0; j < buffer[i].length; j++){
				// append the character at coorindate to the bufferString
				bufferString.append(getCharacter(i,j));
			}
		}
		// push the stringBuffer as a string to the backHistory stack
		backHistory.push(bufferString.toString());
		// clear the forwardHistory
		forwardHistory.clear();
	}
	/** <p>Restore the buffer to the previous recorded state.</p>
	 * */
	public void stepBack(){
		// if their is more than 1 item in the backHistory stack
		if (backHistory.size() > 1){
			// pop from the backHistory stack and push result onto forwardHistory
			forwardHistory.push(backHistory.pop());
			// convert the next item in the backHistory stack to a character array
			String step = backHistory.peek();
			char[] bufferIn = step.toCharArray();
			// int variable counter keeps track of position in character array while iterating through
			int counter = 0;
			// for every row in buffer
			for (int i=0; i < buffer.length; i++){
				// for every column in row in buffer
				for (int j=0; j < buffer[i].length; j++){
					// write the corresponding character in the character array into the buffer
					writeCharacter(i,j,bufferIn[counter]);
					// count up 1
					counter++;
				}
			}
		}else{
			// else warn that their is nothing to undo
			System.out.println("error: nothing to undo");
		}
	}
	/** <p>Restore the buffer to the next recorded state.</p>
	 * */
	public void stepForward(){
		// if forwardHistory is not empty
		if (!forwardHistory.empty()){
			// pop from the forwardHistory stack and push result onto backHistory
			backHistory.push(forwardHistory.pop());
			// convert the next item in the backHistory stack to a character array
			String step = backHistory.peek();
			char[] bufferIn = step.toCharArray();
			// int variable counter keeps track of position in character array while iterating through
			int counter = 0;
			// for every row in buffer
			for (int i=0; i < buffer.length; i++){
				// for every column in row in buffer
				for (int j=0; j < buffer[i].length; j++){
					// write the corresponding character in the character array into the buffer
					writeCharacter(i,j,bufferIn[counter]);
					// count up 1
					counter++;
				}
			}
		}else{
			// else warn that their is nothing to redo
			System.out.println("error: nothing to redo");
		}
	}
}
