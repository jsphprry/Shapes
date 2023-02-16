public class Display{
	/** declaration of buffer array
	 * */
	protected char[][] buffer;
	/** @return Reference to the buffer array
	 * */
	public char[][] returnBuffer(){
		return buffer;
	}
	/** <p>Overwrite the contents of the buffer with the contents of another 2d array of equal size</p>
	 * @param in 2Dimensional array of equal size to buffer to be copied into buffer
	 * @throws ArrayIndexOutOfBoundsException If the 'in' array is smaller than buffer
	 * */
	public void syncBuffer(char[][] in){
		// for every row
		for (int i=0; i < buffer.length; i++){
			// for every column in row
			for (int j=0; j < buffer[i].length; j++){
				// update coordinate with new character
				writeCharacter(i,j,in[i][j]);
			}
		}
	}
	/** <p>Fills the buffer with space characters</p>
	 * */
	public void initialise(){
		// populates buffer with space characters
		initialiseArray(buffer,' ');
	}
	/** <p>Fills a supplied array with one character</p>
	 * @param array 2Dimensional array to be filled with the fill character
	 * @param fill Character to fill array with
	 * */
	public void initialiseArray(char[][] array, char fill){
		// for every row in array
		for(int i = 0; i < array.length; i++){
			// for every column in row in array
			for(int j = 0; j < array[i].length; j++){
				// coordinate is replaced with fill character
				array[i][j] = fill;
			}
		}
	}
	/** @return Character at a specified coordinate y x
	 * @param y y Coordinate to retreive from buffer
	 * @param x x Coordinate to retreive from buffer
	 * */
	public char getCharacter(int y, int x){
		char character = buffer[y][x];
		return character;
	}
	/** <p>Write a supplied character to specified coordinate in buffer</p>
	 * @param y y Coordinate to write at
	 * @param x x Coordinate to write at
	 * @param character Character to be written at y x
	 * */
	public void writeCharacter(int y, int x,char character){
		// check whether the coordinate lies within the bounds of the buffer
		if (y < buffer.length && y >= 0 && x < buffer[y].length && x >= 0){
			// determine the character already at coordinate y x 
			char existingChar = getCharacter(y,x);
			// check whether the coordinate differs from the character to be written
			if (existingChar != character){
				// if they differ, update the coordinate
				buffer[y][x] = character;
			}
		}
	}
	/** <p>Write the contents of a provided 2d array into an area starting from y x into the buffer</p>
	 * @param y y Coordinate to write supplied array from
	 * @param x x Coordinate to write supplied array from
	 * @param area 2Dimensional array to be written into area starting at y x in buffer
	 * @param mode Determines whether to write area with/without space character as 0/1 respectively.
	 * */
	public void writeArea(int y, int x, char[][] area, int mode){
		// for every row in array
		for (int i=0; i < area.length; i++){
			// for every column in row in array
			for (int j=0; j < area[i].length; j++){
				// if mode is 1 and the character to be written is not a space character
				if (area[i][j] != ' ' && mode == 1){
					// write the corresponding character from the provided array into the buffer
					writeCharacter(y+i,x+j,area[i][j]);
				// if the mode is 0
				}else if(mode == 0){
					// write the corresponding character from the provided array into the buffer
					writeCharacter(y+i,x+j,area[i][j]);
				}
			}
		}
	}
}
