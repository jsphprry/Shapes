import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.IntStream;
public class WorkBuffer extends Display{
	// height and width values are stored so they can be used without having to get the buffer dimensions
	private int height;
	private int width;
	// cursor and origin are coordinate used for drawring shapes at a certain point
	private int[] cursor = new int[2];
	private int[] origin = new int[2];
	// m is a variable that represents the gradient of the line drawring method
	private double m;
	// multiply is a set of two variables which represent the stretch in the x and y directions of the curve drawring method
	private double[] multiply = new double[2];
	// clipBoard is a 2d character array that is used to store copied regions of the buffer
	private char[][] clipBoard;
	// reflectMode is a boolean that flips between values as part of the reflect method
	private boolean reflectMode = true;
	/** <p>Initialise buffer and set default values of shape variables</p>
	 * @param y height of buffer
	 * @param x width of buffer
	 * */
	public WorkBuffer(int y, int x){
		// initialise buffer with appropriate dimensions
		buffer = new char[y][x];
		// assign height and width values
		height = y;
		width = x;
		// set cursor position to half the height and half the width
		cursor[0] = y/2;
		cursor[1] = x/2;
		// set gradient value
		m = 1;
		// set stretch multipliers
		multiply[0] = 1;
		multiply[1] = 4;
	}
	/** <p>Return the cursor coordinate</p>
	 * @return Reference to cursor coordinate int[] array.
	 * */
	public int[] returnCursor(){
		return cursor;
	}
	/** <p>Return origin coordinate</p>
	 * @return Reference to origin int[] array
	 * */
	public int[] returnOrigin(){
		return origin;
	}
	/** <p>Return the striaght-line distance between the origin and the cursor</p>
	 * @return Double value of the straight-line distance between the origin coordinate and the cursor coordinate.
	 * */
	public double returnDistance(){
		double distance = Math.pow((Math.pow(origin[0]-cursor[0],2) + Math.pow(origin[1]-cursor[1],2)),0.5);
		return distance;
	}
	/** <p>Return the value of the gradient</p>
	 * @return Gradient value m.
	 * */
	public double returnGradient(){
		return m;
	}
	/** <p>Return the stretch multipliers</p>
	 * @return Reference to the int[] array multiply.
	 * */
	public double[] returnMultiply(){
		return multiply;
	}
	/** <p>Set the cursor coordinates to supplied y and x coords</p>
	 * @param y y coordinate to be set
	 * @param x x coordinate to be set
	 * */
	public void setCursor(int y, int x){
		cursor[0] = y;
		cursor[1] = x;
	}
	/** <p>Set the origin coordinates to the cursor coordinates</p>
	 * */
	public void setOrigin(){
		for (int i = 0; i < cursor.length; i++){
			origin[i] = cursor[i];
		}
	}
	/** <p>Handling for user input in certain control loops that deals with positioning cursor</p>
	 * @param input User input as string
	 * */
	public void positionCursor(String input){
		try{
			// check input for goto command
			// if goto found
			if (input.contains("goto")){
				// split input by regex expression [ /] ie space and slash characters
				String[] values = input.split("[ /]");
				// convert values in string array into int values
				int[] intValues = new int[3];
				for (int i = 1; i < values.length ; i++){
					intValues[i] = Integer.parseInt(values[i]);
				}
				// set the cursor coorindates to the parsed values
				setCursor(intValues[2],intValues[1]);
			// goto not found
			}else{
				// split input by / character
				String[] splitInput = input.split("/");
				// if the result contains more than one value
				if (splitInput.length > 1){
					// parse the second item as an int and repeat moveCursor for result times
					for (int i=0; i < Integer.parseInt(splitInput[1]); i++){
						moveCursor(splitInput[0]);
					}
				// if the result contains only one value
				}else{
					// moveCursor with input being the first value of result.
					moveCursor(splitInput[0]);
				}
			}
		// if parseInt encounters a problem then catch and warn of error.
		}catch(NumberFormatException e){
				System.out.println("error: Illegal argument expected integer."); 
		}
	}
	/** <p>Bound the cursor coordinates to within the size of the buffer</p>
	 * @param buffer Reference to buffer
	 * */
	public void checkForBoundary(char[][] buffer){
		// if y coordinate is greater than max bound
		if (cursor[0] >= buffer.length){
			// cursor y set to max bound
			cursor[0] = buffer.length - 1;
		// if y coordinate is less than min bound
		}else if (cursor[0] < 0){
			// cursor is set to min bound
			cursor[0] = 0;
		}
		// if x coordinate is greater than max bound
		if (cursor[1] >= buffer[cursor[0]].length){
			// cursor x set to max bound
			cursor[1] = buffer[cursor[0]].length - 1;
		// if x coordinate is less than min bound
		}else if (cursor[1] < 0){
			// cursor is set to min bound
			cursor[1] = 0;
		}
	}
	/** <p>Function for moving cursor incrementally</p>
	 * @param input User input as string
	 * */
	public void moveCursor(String input){
		// check input
		switch (input){
			case "i":
				// cursor y - 1
				cursor[0]--;
				break;
			case "j":
				// cursor x - 1
				cursor[1]--;
				break;
			case "k":
				// cursor y + 1
				cursor[0]++;
				break;
			case "l":
				// cursor x + 1
				cursor[1]++;
				break;
			default:
				// if no case is met then issue warning
				System.out.println("Unrecognised input:" + input);
		}
	}
	/** <p>Function for altering the gradient of a line</p>
	 * @param input User input as string
	 * */
	public void positionLine(String input){
		// check input
		switch (input){
			case "i":
				// gradient + 0.5
				m = m + 0.5;
				break;
			case "k":
				// gradient - 0.5
				m = m - 0.5;
				break;
			default:
				// if no case is met then issue warning
				System.out.println("Unrecognised input:" + input);
		}
	}
	/** <p>Function for altering the stretch multipliers of the curve</p>
	 * @param input User input as string
	 * */
	public void positionCurve(String input){
		// check input
		switch (input){
			case "i":
				// stretch y + 1
				multiply[1]++;
				break;
			case "j":
				// stretch x - 1
				multiply[0]--;
				break;
			case "k":
				// stretch y - 1
				multiply[1]--;
				break;
			case "l":
				// stretch x + 1
				multiply[0]++;
				break;
			default:
				// if no case is met then issue warning
				System.out.println("Unrecognised input:" + input);
		}
	}
	/** <p>Draws cursor onto screen at cursor coorindate</p>
	 * @param brush Characters to be written
	 * */
	public void drawCursor(String brush){
		// split input string into character array
		char[] brushSplit = brush.toCharArray();
		// for the length of the array iterate throuth the character writing each to an adjacent position in the buffer
		for (int i = 0; i < brushSplit.length; i++){
			writeCharacter(cursor[0],cursor[1] + i,brushSplit[i]);
		}
		// print the cursor coordinates.
		System.out.println(cursor[1] + "," + cursor[0]);
	}
	/** <p>Draw Shaded circle from origin to radius of distance(origin -> cursor)</p>
	 * @param A Character representing illuminated-surface
	 * @param B Character representing shaded-surface
	 * @param C Character representing dark-surface
	 * */
	public void drawShadedCircle(char A, char B, char C){
		// calculate distance between origin and cursor
		double r = returnDistance();
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// at every point
				// calclate the distance of the current coordinate to 0,0
				double lightDist = Math.pow((Math.pow(i,2) + Math.pow(j,2)),0.5);
				// calculate the distance between 0,0 and the origin point
				double objectDist = Math.pow((Math.pow(origin[0],2) + Math.pow(origin[1],2)),0.5);
				// calculate the distance between the current point and the origin point
				double dist = Math.pow((Math.pow(i-origin[0],2) + Math.pow(j-origin[1],2)),0.5);
				// if the distance between coordinate and origin is less than distance between cursor and origin, decide which character to write to the coordinate
				if (dist < r){
					// if the distance of the current coordinate to 0,0 is less than the distance from 0,0 to the origin, then colour with character A
					if (lightDist < objectDist){
						writeCharacter(i,j,A);
					// if the distance of the current coordinate to 0,0 is less than the distance from 0,0 to the origin plus half the radius, then colour with character B
					}else if (lightDist < objectDist + r / 2){
						writeCharacter(i,j,B);
					// if neither condition is met then colour with character C
					}else{
						writeCharacter(i,j,C);
					}
				}
			}
		}
		/*DEBUG*///buffer[origin[0]][origin[1]] = 'A';
		/*DEBUG*///buffer[origin[0]-r][origin[1]] = 'r';
	}
	/** <p>Draw circle from origin to radius of distance(origin -> cursor)</p>
	 * @param inner Character to fill circle.
	 * */
	public void drawCircle(char inner){
		// calculate distance between origin and cursor
		double r = returnDistance();
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// at every point
				// calculate the distance between the current point and the origin point
				double dist = Math.pow((Math.pow(i-origin[0],2) + Math.pow(j-origin[1],2)),0.5);
				// if the distance between coordinate and origin is less than distance between cursor and origin, write the supplied character to the coordinate
				if (dist < r){
					writeCharacter(i,j,inner);
				}
			}
		}
		/*DEBUG*///buffer[origin[0]][origin[1]] = 'A';
		/*DEBUG*///buffer[origin[0]][origin[1]+r] = 'r';
	}
	/** <p>Draw line at cursor coordinates</p>
	 * @param inner Character to fill line.
	 * */
	public void drawLine(char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// if the |gradient| is greater than 4
				if (m > 4 || m < -4){
					// fill coordinates with x equal to cursor x with supplied character
					writeCharacter(i,cursor[1],inner);
				}else{
					// if the coordinate satisfies the equation y = m * x + c
					if (cursor[0] - i == m*(j - cursor[1])){
						// write supplied character to coordinate
						writeCharacter(i,j,inner);
					}
				}
			}
		}
	}
	/** <p>Draw curve at cursor coordinates</p>
	 * @param pow Power to which the curve is rasied. For example 2 mean y=x^2 and 3 means y=x^3. 
	 * @param inner Character to fill the curve.
	 * */
	public void drawCurve(double pow,char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// if the coordinate satisfies the equation Yx(y - y1) = Mx(-(x - x1))^2 or more simply y=x^2 within a range of +-5
				if (multiply[1]*(cursor[0] - i) + 5 >= multiply[0]*Math.pow(-(cursor[1] - j), pow) - 5 && multiply[1]*(cursor[0] - i) - 5 <= multiply[0]*Math.pow(-(cursor[1] - j), pow) + 5){
					// write the supplied character to the coordinate
					writeCharacter(i,j,inner);
				}
	    	}
	    }
	}
	/** <p>Draw partially-obscured circle. A normal line at the bisector of the line drawn between the origin and the cursor acts as a veil to obscure a portion of the circle.</p>
	 * @param r Radius of circle
	 * @param inner character to fill the shape.
	 * */
	public void drawPhase(double r, char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// calculate the distance between the coordinate and the origin
				double dist0 = Math.pow((Math.pow(i-origin[0],2) + Math.pow(j-origin[1],2)),0.5);
				// calculate the distance between the coordinate and the cursor
				double dist1 = Math.pow((Math.pow(i-cursor[0],2) + Math.pow(j-cursor[1],2)),0.5);
				// if the distance between the coordinate and the origin is less than the supplied radius and the distance from the coordinate to the cursor is greater than the distance between the coordinate and the origin
				if (dist0 < r && dist0 < dist1){
					// fill the coordinate with the supplied character
					writeCharacter(i,j,inner);
				}
				/*DEBUG*///else if (dist0 == dist1){
				/*DEBUG*///	buffer[i][j] = '.';
				/*DEBUG*///}
			}
		}
		/*DEBUG*///buffer[origin[0]][origin[1]] = 'A';
		/*DEBUG*///buffer[cursor[0]][cursor[1]] = 'B';
		/*DEBUG*///buffer[origin[0]][origin[1]-r] = 'r';
	}
	/** <p>Draw Rectangle within the range of the max and min y values and within the range of the max and min x values of the origin and cursor</p>
	 * @param inner Character to fill shape.
	 * */
	public void drawRectangle(char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// if the coordinate lies within the range of the max and min y values and the coordinate lies within the range of the max and min x values
				if (i >= Math.min(origin[0],cursor[0]) && i <= Math.max(origin[0],cursor[0])){
					if (j >= Math.min(origin[1],cursor[1]) && j <= Math.max(origin[1],cursor[1])){
						// write the suplied character to the coordinate
						writeCharacter(i,j,inner);
					}
				}
			}
		}
	}
	/** <p>Draw Cross centered at the cursor coordinates</p>
	 * @param inner Character to fill shape.
	 * */
	public void drawCross(char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// if the calculation inputs aren't going to cause a divide by zero error
				if (cursor[1] - j != 0){
					// and the equation |y-y1| / |x - x1| = 1 is satisfied
					if (Math.abs(cursor[0] - i)/Math.abs(cursor[1] - j) ==  1){
						// write the suplied character to the coordinate
						writeCharacter(i,j,inner);
					}
				}
			}
		}
	}
	/** <p>Draw oval between the origin and the cursor coordinates</p>
	 * @param roundness Multiplier used in deciding the shape of the oval
	 * @param inner Character to fill the shape
	 * */
	public void drawOval(double roundness, char inner){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// calculate the distance between the coordinate and the origin
				double dist0 = Math.pow((Math.pow(i-origin[0],2) + Math.pow(j-origin[1],2)),0.5);
				// calculate the distance between the coordinate and the cursor
				double dist1 = Math.pow((Math.pow(i-cursor[0],2) + Math.pow(j-cursor[1],2)),0.5);
				// calculate distance between origin and cursor
				double dist01 = returnDistance();
				// if the distance between the coordinate and the origin plus the distance between the coordinate and the cursor is less than the distance between origin and cursor multiplied by the 'roundness'
				if (dist0+dist1 < dist01 * roundness){
					// write the suplied character to the coordinate
					writeCharacter(i,j,inner);
				}
			}
		}
		/*DEBUG*///buffer[origin[0]][origin[1]] = "A";
		/*DEBUG*///buffer[cursor[0]][cursor[1]] = "B";
	}
	/** <p>Draw box within the range of the max and min y values and within the range of the max and min x values of the origin and cursor</p>
	 * @param edge0 Character to draw box edge with by default
	 * @param edge1 Alternative character to draw box edge with in the case where character being overwritten is equal edge0
	 * */
	public void drawBox(char edge0, char edge1){
		// iterate through every coordinate in the buffer
		for(int i = 0; i < buffer.length; i++){
			for(int j = 0; j < buffer[i].length; j++){
				// if the coordinate lies exactly on the ranges between the min and max x and y coordinates
				if (i >= Math.min(origin[0],cursor[0]) && i <= Math.max(origin[0],cursor[0]) && j >= Math.min(origin[1],cursor[1]) && j <= Math.max(origin[1],cursor[1]) && (i == origin[0] || j == origin[1] || i == cursor[0] || j == cursor[1])){
					//decide which character to write
					// if the character to be overwritten is the same as the main supplied character, then write the alternative supplied character
					if (buffer[i][j] == edge0){
						writeCharacter(i,j,edge1);
					// otherwise write the main supplied character
					}else{
						writeCharacter(i,j,edge0);
					}
				}
			}
		}
	}
	/** <p>Generate a list of coordinates found under certain conditions using a boundary fill style algorithm</p>
	 * @param mode Accepts values 0 or 1 to determine whether to search for like characters or to search for an item respectively.
	 * @param range The distance that is checked up, down, left and right of a coordinate that is to be checked
	 * @param includeCorners Boolean value determining whether to include the 'corners' of the search area at the end of the list.
	 * @return List of int[] arrays representing coordinates in the buffer that have been deemed suitable in the search
	 * */
	public List<int[]> boundarySearch(int mode, int range, boolean includeCorners){
		// declare and initialise list of int arrays
		List<int[]> suitableCoords = new ArrayList<>();
		// initialise int array smallest with the cursor coordinates
		int[] smallest = {cursor[0],cursor[1]};
		// initialise int array largest with the cursor coordinates
		int[] largest = {cursor[0],cursor[1]};
		// intitialise stack of int arrays
		Stack<int[]> coordinates = new Stack<>();
		// add the cursor coordinates to the stack
		coordinates.add(cursor);
		// determine the character at the cursor coordinates in the buffer
		char find = getCharacter(cursor[0],cursor[1]);
		// begin while loop that runs until the stack is empty
		while(!coordinates.empty()){
			// pop from the stack and store result as int array currentPosition
			int[] currentPosition = coordinates.pop();
			// set x and y variables to the values in this array
			int y = currentPosition[0];
			int x = currentPosition[1];
			// get the character in the buffer at the coordinates
			char current = getCharacter(y,x);
			/*DEBUG*///System.out.println(((current.equals(find) && !containsIntArray(suitableCoords, currentPosition) && mode == 0) || (!current.equals(" ") && !containsIntArray(suitableCoords, currentPosition) && mode == 1)) + " " + uniqueString);
			//
			// for situation where mode = 0, proceed if the character at the currentPosition is equal to the original character 'find'
			// for situation where mode = 1, proceed if the character at the currentPosition is not a space character
			// in both situations proceed only if the currentPosition coordinates are not already in the list of suitable coordinates.
			if((current == find && !listContainsIntArray(suitableCoords, currentPosition) && mode == 0) || (current != ' ' && !listContainsIntArray(suitableCoords, currentPosition) && mode == 1)){
				// if condition is met then add the currentPosition to the list of suitable coordinates
				suitableCoords.add(currentPosition);
				// check if the x and y coordinates are either greater or larger than the smallest and largest x and y values
				if (y < smallest[0]){
					smallest[0] = y;
				}else if (y > largest[0]){
					largest[0] = y;
				}
				if (x < smallest[1]){
					smallest[1] = x;
				}else if (x > largest[1]){
					largest[1] = x;
				}
				// for each coordinate up, down, left and right of the currentPosition for distance 'range', if the values do not lie outside the buffer size then add the coordinates as an int array to the stack.
				for (int i=1; i <= range; i++){
					if (x + i < buffer[y].length){
						int[] coord = {y,x+i};
						coordinates.push(coord);
					}
					if (x - i >= 0){
						int[] coord = {y,x-i};
						coordinates.push(coord);
					}
					if (y + i < buffer.length){
						int[] coord = {y+i,x};
						coordinates.push(coord);
					}
					if (y - i >= 0){
						int[] coord = {y-i,x};
						coordinates.push(coord);
					}
				}
			}
		}
		// if the includeCorners boolean is true at the end of the method
		if (includeCorners){
			// add the largest and smallest coordinates to the end of the list of suitable coordinates.
			suitableCoords.add(largest);
			suitableCoords.add(smallest);
		}
		// return the list of suitable coordinates
		return suitableCoords;
	}
	/** <p>Search a list for an specific int array</p>
	 * @param list List of int[] arrays
	 * @param array int[] array to search list for.
	 * @return True if the list contains the provided array, false if not.
	 * */
	private boolean listContainsIntArray(List<int[]> list, int[] array){
		boolean result = false;
		// for every item in the list
		for (int i=0; i < list.size(); i++){
			// if the array at position i is populated with the same values as the supplied int array
			if (Arrays.equals(list.get(i), array)){
				// set result to false
				result = true;
				// break for loop
				break;
			}
		}
		//return true or false
		return result;
	}
	/** <p>Boundary fill an area of the buffer</p>
	 * @param replace Character to substitute into the boundary fill coordinates
	 * */
	public void boundaryFill(char replace){
		// generate a list of suitable coordinates with mode 0 and without corners included
		List<int[]> suitableCoords = boundarySearch(0,1,false);
		// for every item in the list
		for (int i=0; i < suitableCoords.size(); i++){
			// retrieve coordinates
			int[] coordinate = suitableCoords.get(i);
			// write the supplied character to the coordinates
			writeCharacter(coordinate[0],coordinate[1],replace);
		}
	}
	/** <p>Shaded boundary fill an area of the buffer</p>
	 * @param A Character representing illuminated-surface
	 * @param B Character representing shaded-surface
	 * @param C Character representing dark-surface
	 * */
	public void boundaryFillShaded(char A, char B, char C){
		// generate a list of suitable coordinates with mode 0 and without corners included
		List<int[]> suitableCoords = boundarySearch(0,1,false);
		// calculate distance between 0,0 and the cursor
		double objectDist = Math.pow((Math.pow(cursor[0],2) + Math.pow(cursor[1],2)),0.5);
		// for every item in the list
		for (int i=0; i < suitableCoords.size(); i++){
			// retrieve coordinates
			int[] coordinate = suitableCoords.get(i);
			// calculate distance between the coordinates and 0,0
			double lightDist = Math.pow((Math.pow(coordinate[0],2) + Math.pow(coordinate[1],2)),0.5);
			// if the distance between the coordinates and 0,0 is less than the distance between 0,0 and the cursor
			if (lightDist < objectDist){
				// write character A at coordinate
				writeCharacter(coordinate[0],coordinate[1],A);
			// if the distance between the coordinates and 0,0 is less than the distance between 0,0 and the cursor plus 5
			}else if (lightDist < objectDist + 5){
				// write character B at coordinate
				writeCharacter(coordinate[0],coordinate[1],B);
			// otherwise if neither condition is met
			}else{
				// write character C at coordinate
				writeCharacter(coordinate[0],coordinate[1],C);
			}
		}
	}
	/** <p>Copy an item from the buffer</p>
	 * <p>Where an item is a group of coorindates surrounded by space characters.</p>
	 * @param range Value passed to boundarySearch. See boundarySearch.
	 * */
	public void copyItem(int range){
		// generate a list of suitable coordinates with mode 1 and with corners included
		List<int[]> suitableCoords = boundarySearch(1,range,true);
		// store the end values of the list as smallest and largest int arrays
		int[] smallest = suitableCoords.get(suitableCoords.size() - 1);
		int[] largest = suitableCoords.get(suitableCoords.size() - 2);
		// calculate range in x and y plane using these values.
		int lenY = largest[0] - smallest[0] + 1;
		int lenX = largest[1] - smallest[1] + 1;
		// intitialise the clipBoard with dimensions lenX and lenY
		clipBoard = new char[lenY][lenX];
		// fill the clipBoard with space characters
		initialiseArray(clipBoard,' ');
		// for every item in the list excluding the last two in which the corner coordinates are stored
		for (int i=0; i < suitableCoords.size() - 2; i++){
			// retrieve coordinates
			int[] coordinate = suitableCoords.get(i);
			// write the values at the coordinates in the buffer to the relative position in the clipBoard array
			clipBoard[coordinate[0] - smallest[0]][coordinate[1] - smallest[1]] = buffer[coordinate[0]][coordinate[1]];
		}
	}
	/** <p>Erase an item from the buffer</p>
	 * <p>Where an item is a group of coorindates surrounded by space characters.</p>
	 * @param range Value passed to boundarySearch. See boundarySearch.
	 * */
	public void eraseItem(int range){
		// generate a list of coordinates with mode 1 and with corners not included
		List<int[]> suitableCoords = boundarySearch(1,range,false);
		// for every item in the list
		for (int i=0; i < suitableCoords.size(); i++){
			// retrieve coordinates
			int[] coordinate = suitableCoords.get(i);
			// write space character to coordinates
			writeCharacter(coordinate[0],coordinate[1],' ');
		}
	}
	/** <p>Copy region from buffer within the range of the max and min y values and within the range of the max and min x values of the cursor and origin</p>
	 * */
	public void copy(){
		// calculate the maximum and minimum x values of the cursor and origin
		int maxX = Math.max(origin[1],cursor[1]);
		int minX = Math.min(origin[1],cursor[1]);
		// calculate the maximum and minimum y values of the cursor and origin
		int maxY = Math.max(origin[0],cursor[0]);
		int minY = Math.min(origin[0],cursor[0]);
		// calculate the range of these values in their respective plane
		int lenX = maxX - minX + 1;
		int lenY = maxY - minY + 1;
		// intitialise the clipBoard with these dimensions
		clipBoard = new char[lenY][lenX];
		// for every coordinate in the range of the max and min x and y values
		for (int i = minY; i <= maxY; i++){
			for (int j = minX; j <= maxX; j++){
				// copy the character at the coordinate in the buffer into the relative clipBoard coordinate
				clipBoard[i - minY][j - minX] = buffer[i][j];
			}
		}
		/*DEBUG*///System.out.println(maxX + "," + maxY + " " + minX + "," + minY + " " + lenX + "," + lenY);
	}
	/** <p>Paste clipBoard at cursor coordinates</p>
	 * @param mode Value 0 or 1 determines whether to paste with 'blankspace' as in space characters, where 0 is with spaces and 1 is without
	 * */
	public void paste(int mode){
		try{
			// write clipBoard array to the buffer starting from the cursor coordinates
			writeArea(cursor[0],cursor[1],clipBoard,mode);
		// if clipBoard has not been initialised catch nullPointer and warn
		}catch(NullPointerException e){
			System.out.println("error: Could not paste, no area selected");
		}
	}/** <p>Tile the clipBoard across the buffer</p>
	 * @param mode Value 0 or 1 determines whether to tile with 'blankspace' as in space characters, where 0 is with spaces and 1 is without
	 * */
	public void tile(int mode){
		try{
			// iterate through every coordinate in the buffer
			for (int i=0; i < buffer.length; i++){
				for (int j=0; j < buffer[i].length; j++){
					// if the coordinates are a multiple of the clipboard width and height
					if (i % clipBoard.length == 0 && j % clipBoard[0].length == 0){
						// write clipBoard array to the buffer starting from the cursor coordinates
						writeArea(i,j,clipBoard,mode);
					}
				}
			}
		// if clipBoard has not been initialised catch nullPointer and warn
		}catch(NullPointerException e){
			System.out.println("error: Could not tile, no area selected");
		}
	}
	/** <p>Rotate the contents of the buffer by 90 degrees clockwise or anti-clockwise</p>
	 * @param mode Determines whether to rotate clockwise/anti-clockwise as 0/1 respectively. 
	 * */
	public void rotate(int mode){
		// declare and initialise a 2d array width dimensions equal to the buffer
		char[][] canvas = new char[height][width];
		// iterate through every coordinate in the buffer
		for (int i=0; i < buffer.length; i++){
			for (int j=0; j < buffer[i].length; j++){
				// rotating the buffer requires the buffer to be reflected along a virtual diagonal line that is mirrored in the vertical axis for every buffer rotation of 90 degrees.
				if ((mode == 0 && reflectMode) || (mode == 1 && !reflectMode)){
					// reflect the coordinates along the virtual diagonal from yx 0,0 to max-height,max-width (\)
					canvas[i][j] = getCharacter(j,i);
				}else if ((mode == 0 && !reflectMode) || (mode == 1 && reflectMode)){
					// reflect the coordinates along the virtual diagonal from yx 0,max-width to max-height,0 (/)
					canvas[buffer.length-1 - i][buffer[i].length-1 - j] = getCharacter(j,i);
				}
			}
		}
		// once reflected flip the reflectMode
		reflectMode = !reflectMode;
		// sync the buffer with the canvas array
		syncBuffer(canvas);
		// depending on the angle of the virtual diagonal, it is next neccasary to mirror the buffer either vertically or horizontally in order to finish the rotation.
		if (reflectMode){
			// vertical mirror
			mirror(1);
		}else if (!reflectMode){
			// horizontal mirror
			mirror(0);
		}
	}
	/** <p>Mirror the contents of the buffer horizontally or vertically</p>
	 * @param mode Determines whether to mirror horizontally/vertically as 0/1 respectively. 
	 * */
	// 
	public void mirror(int mode){
		// declare and initialise a 2d array width dimensions equal to the buffer
		char[][] canvas = new char[height][width];
		// iterate through every coordinate in the buffer
		for (int i=0; i < buffer.length; i++){
			for (int j=0; j < buffer[i].length; j++){
				// depending on the mode specified
				if (mode == 0){
					// copy characters from left of buffer to right of canvas and characters from right of buffer to left of canvas
					canvas[i][buffer[i].length-1 - j] = getCharacter(i,j);
				}else if (mode == 1){
					// copy characters from top of buffer to bottom of canvas and characters from bottom of buffer to top of canvas
					canvas[buffer.length-1 - i][j] = getCharacter(i,j);
				}
			}
		}
		// sync the buffer with the canvas array
		syncBuffer(canvas);
	}
	/** <p>Draw mandelbrot onto buffer</p>
	 * @param xPos Determines ofset of view from x=0
	 * @param yPos Determines ofset of view from y=0
	 * @param scale Determines the zoom factor of the Mandelbrot
	 * */
	public void mandelbrot(double xPos, double yPos, double scale){
		//long start = System.nanoTime();
		// Using streams create 2d array where each coordinate is filled by an appropriate character
		Character[][] canvas = 
			// Create intStream of 0 to buffer height and map each number to intStream object as 'y'
			IntStream.range(0, buffer.length).mapToObj(y -> 
				// Create intStream of 0 to buffer width and map each number to mandelbrotCalculation as 'x'
				IntStream.range(0, buffer[y].length).mapToObj(x -> 
					// perform mandelBrot calculation using supplied values for position and zoom
					mandelbrotCalculation(y,x,xPos,yPos,scale)
				// perform operations in parallel
				).parallel()
				// Collect returned characters as Character[] array
				.toArray(Character[]::new)
			// perform operations in parallel
			).parallel()
			// Collect returned arrays as Character[][] array
			.toArray(Character[][]::new)
		;
		/*long stop = System.nanoTime();
		double time = (double) (stop-start)/1_000_000;
		System.out.println("\nTime elapsed: " + time + " ms");*/
		// iterate through every coordinate in buffer
		for (int i=0; i < buffer.length; i++){
			for (int j=0; j < buffer[i].length; j++){
				// copy char value of each relative canvas Character value into the buffer
				buffer[i][j] = canvas[i][j].charValue();
			}
		}
	}
	/** Escape time algorithm for Mandelbrot calculations
	 * @param i y coordinate in buffer in which calculation is being performed
	 * @param j x coordinate in buffer in which calculation is being performed
	 * @param xPos Determines ofset of view from x=0
	 * @param yPos Determines ofset of view from y=0
	 * @param scale Determines the zoom factor of the Mandelbrot
	 * @return Character from pre-determined pallete, representing the number of iterations taken before the escape condition was met. Ordered by percieved brightness.
	 * */
	// Adapted from https://en.wikipedia.org/wiki/Plotting_algorithms_for_the_Mandelbrot_set
	private char mandelbrotCalculation(int i, int j, double xPos, double yPos, double scale){
		// create char array containing characters ordered by percieved 'brightness'
		char[] pallete = {' ','.',':','\'',';','|'};
		// scale supplied coordinates i and j as x0 and y0 so that they are proportional to the scale that the calculation requires
		double x0 = j*(scale/(buffer[i].length - 1)) + xPos;
		double y0 = i*(scale/(buffer.length - 1)) + yPos;
		// two real numbers x and y simulate complex number operations
		double x = 0;
		double y = 0;
		// set iteration value as 0
		int iteration = 0;
		// set the maximum number of iterations before escape condition
		int max_iteration = 1000;
		// perform calculation, increasing the iteration count every time until either max iteration value is reached or the sum of the squares of the two numbers x and y exceeds 2^2 as 4.
		while (x*x + y*y <= 4 && iteration < max_iteration){
			double xtemp = x*x - y*y + x0;
			y = 2*x*y + y0;
			x = xtemp;
			iteration++;
		}
		/*DEBUG*///System.out.print(pallete[(iteration-1) % pallete.length]);
		/*DEBUG*///try{Thread.sleep(5);}catch(Exception e){}
		//
		// return the character at the pallete location equal to (iteration-1) % pallet-length
		return pallete[(iteration-1) % pallete.length];
		/* 
		 * Abandoned attempt at using BigDecimal as the data type for variables in calculation.
		 * 
		 * */
		/*BigDecimal x0 = BigDecimal
			.valueOf(j).multiply(
				scale.divide(
					BigDecimal.valueOf(
						buffer[i].length - 1
					), MathContext.DECIMAL128
				)
			).add(xPos)
		;
		BigDecimal y0 = BigDecimal
			.valueOf(i).multiply(
				scale.divide(
					BigDecimal.valueOf(
						buffer[i].length - 1
					), MathContext.DECIMAL128
				)
			).add(yPos)
		;
		BigDecimal x = BigDecimal.valueOf(0);
		BigDecimal y = BigDecimal.valueOf(0);
		int iteration = 0;
		int max_iteration = 1000;
		while ((x.pow(2).add(y.pow(2)))
			.compareTo(BigDecimal.valueOf(4)) < 1 && iteration < max_iteration){
			System.out.println(iteration + " " + (x.pow(2).add(y.pow(2))).compareTo(BigDecimal.valueOf(4)));
			BigDecimal xtemp = x.pow(2).subtract(y.pow(2)).add(x0);
			y = (BigDecimal.valueOf(2).multiply(x).multiply(y)).add(y0);
			x = xtemp;
			iteration++;
		}*/
	}
}
