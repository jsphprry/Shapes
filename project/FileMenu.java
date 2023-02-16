import java.lang.StringBuffer;
import java.util.HashMap;
import java.util.Map;
public class FileMenu extends Display{
	// Height and Width are used to store the values of the height and width of the buffer, so the sizes can be used in calculations without having to use the .length method on the buffer
	private int Height;
	private int Width;
	// size, columns and padding are styling variables that determine how the file menu looks. Where size is the size of the thumbnails, columns represents the number of columns that the files in the menu are sorted into and padding is the number of coordinates added to the edges of certain items.
	private int size;
	private int columns;
	private int padding;
	// scroll represents the file that the menu is drawn from
	private int scroll;
	// menuPosition represents the file that is currently selected in the menu
	private int menuPosition;
	// viewLimit represents the maximum files that can fit on the screen given the styling values set. Limits the number of files that are thumbnailed on every draw of the menu.
	private int viewLimit;
	// positionLimit is the maximum distance of menuPosition from scroll that can be reached before the scoll value increases and the menu moves downwards.
	private int positionLimit;
	// declare fileHandling object for reading files
	private FileHandling fileHandler;
	/** <p>Initialise buffer and set default style variables</p>
	 * @param y height of buffer
	 * @param x width of buffer
	 * @param Size Thumbnail size in file menu
	 * @param Columns Number of columns in file menu
	 * @param Padding Padding around certain items in the menu
	 * */
	public FileMenu(int y, int x, int Size, int Columns, int Padding){
		// initialise buffer with appropriate dimensions
		buffer = new char[y][x];
		// assign height and width values
		Height = y;
		Width = x;
		// initialise fileHandler with values
		fileHandler = new FileHandling(12,y,x);
		// set size variable to supplied value Size
		size = Size;
		// set columns variable to supplied value Columns
		columns = Columns;
		// set padding variable to supplied value Padding
		padding = Padding;
		// set scroll value to 0
		scroll = 0;
		// set menuPosition to 0
		menuPosition = 0;
		// calculate viewLimit  
		viewLimit = (int)Math.floor((double)buffer.length / size) * columns;
		// calculate positionLimit
		positionLimit = (int)Math.floor((double)buffer.length / (size + 2*padding)) * columns;
	}
	/** @return int value of the menuPosition
	 * */
	public int returnMenuPosition(){
		return menuPosition;
	}
	/** <p>Method for navigating menu</p>
	 * @param input User input to be processed
	 * @param listLength Number of files contained within the displayed directory
	 * */
	public void navigateMenu(String input, int listLength){
		// try to convert input value to an intger
		try{
			// set menuPosition to the integer converted value of the input
			menuPosition = Integer.parseInt(input);
			// set the scroll value to the parsed input rounded down to the nearest multiple of columns 
			scroll = (int)Math.floor((double)Integer.parseInt(input)/columns)*columns;
		}catch (NumberFormatException e){
			// if the integer conversion throws an error
			switch (input){
				// in the case that input is i
				case "i":
					// set the menuPosition appropriately
					menuPosition = menuPosition - columns;
					// exit switch statement
					break;
				case "j":
					// set the menuPosition appropriately
					menuPosition = menuPosition - 1;
					// exit switch statement
					break;
				case "k":
					// set the menuPosition appropriately
					menuPosition = menuPosition + columns;
					// exit switch statement
					break;
				case "l":
					// set the menuPosition appropriately
					menuPosition = menuPosition + 1;
					// exit switch statement
					break;
				// if the case is unrecognised then warn
				default:
					System.out.println("error: Unrecognised input: " + input);
			}
		}
		// scroll the menu upwards when top of view is reached
		// if the menuPosition exceeds the sum of scoll and positionLimit and the scroll is less than the listLength minus positionLmiit
		if (menuPosition >= scroll + positionLimit && scroll <  listLength - positionLimit){
			// increase the scroll value by columns
			scroll = scroll + columns;
		}
		// scroll the menu downwards when bottom of view is reached
		// if the menuPosition is less than the scroll value and the change that is to made to the scroll value does not result less than or equal zero
		if (menuPosition < scroll && scroll - columns >= 0){
			// decrease the scroll value by columns
			scroll = scroll - columns;
		}
		// prevent the menuPosition from moving past the start of the file list
		// if the menuPosition is less than 0
		if (menuPosition < 0){
			// set menuPosition to 0
			menuPosition = 0;
		}
		// prevent the menuPosition from moving past the end of the file list
		// if the menuPosition is greater or equal the number of files
		if (menuPosition >= listLength){
			// set the menuPosition
			menuPosition = listLength - 1;
		}
		// when the bottom of the file list is reached in the view the scroll scrolls no further
		// when the sum of scroll and positionLimit is greater or equal the listLength and the listLength is greater than the position limit is greater tham the positionLimit
		if (scroll + positionLimit >= listLength && listLength > positionLimit){
			// set the scroll to the length of the file list minux the positionLimit
			scroll = listLength - positionLimit;
			// round the value of scroll up to the nearest multiple of columns
			while (scroll % columns != 0){
				scroll++;
			}
		}
	}
	/** <p>Method for painting the menu into the fileMenu screen buffer</p>
	 * @param directory Directory containing the saved files to be displayed
	 * */
	public void paintView(String directory){
		// initialise the buffer
		initialise();
		// retrieve the files in the directory as array of strings
		String[] files = fileHandler.getFiles(directory);
		// declare 2D char array item
		char[][] item;
		// modifiers used for converting the file index into a coordinate on the buffer to write items to as y x
		int[] relativeModifier = {
			size + 2*padding,
			((Width - (2*columns*padding))/columns) + 2*padding
		};
		// for the number of items that can be drawn in the view with the dimensions of the items
		for (int index=0; index < viewLimit && index + scroll < files.length; index++){
			// create pathname for the file that is to be drawn
			String file = directory + files[index + scroll];
			// convert the index into a relative coordinate in the buffer
			int[] relativePosition = {
				(int)Math.floor((double)index/columns) * relativeModifier[0],
				index % columns * relativeModifier[1]
			};
			// if the item in the view is equal the menuPosition
			if (index + scroll == menuPosition){
				// create item with ` as background
				item = makeItem(index + scroll,file,'`');
			// otherwise
			}else{
				// create item with blank background
				item = makeItem(index + scroll,file,' ');
			}
			// write the item array into the buffer at the realtive coordinate
			writeArea(relativePosition[0],relativePosition[1],item,0);
		}
	}
	/** <p>Method for creating a scrollbar representing the selected position's relative position in the file list</p>
	 * @param length Horizontal length of the scroll bar to be made
	 * @param limit The length of the file list
	 * @param handle The character to represent the current position in the file list
	 * @param bar The character to represent the rest of the scroll bar
	 * @return String containing the scrollbar formatted with supplied parameters
	 * */
	public String makeScrollbar(int length, int limit, char handle, char bar){
		// create empty StringBuffer object
		StringBuffer scrollbar = new StringBuffer("");
		// calculate relative position in the menu position based on the file list length and the size of the scroll bar
		int scrollBarPosition = (int)Math.floor(length * ((double)menuPosition / limit));
		// for the length of the scrollbar minus the padding
		for (int i=-padding; i < length-padding; i++){
			// if the iteration position i is between the range scrollBarPosition (+-) padding
			if (i >= scrollBarPosition - padding && i <= scrollBarPosition + padding){
				// append the handle character to the string
				scrollbar.append(handle);
			// otherwise
			}else{
				// append the default scroll bar character
				scrollbar.append(bar);
			}
		}
		// return the StringBuffer as a string
		return scrollbar.toString();
	}
	/** <p>Create file item to write in menu.</p>
	 * <p>Item consists of file thumbnail, name and size information</p>
	 * @param fileNumber the 0-indexed position of the file in the file list
	 * @param file Pathname of the file that is to be read
	 * @param background Character for the item
	 * @return 2 Dimensional char array containing the generated thumbnail for the file and the file name and size
	 * */
	public char[][] makeItem(int fileNumber, String file, char background){
		// declare and initialise 2D char array 'canvas' with appropriate dimensions 
		char[][] canvas = new char[size + (2 * padding)][Width/columns];
		// populate the character array with the background character
		initialiseArray(canvas,background);
		// create thumbnail for file at pathname 'file'
		char[][] thumbnail = makeThumbnail(file,size);
		// define the coordinates in the canvas to write the thumbnail
		int[] thumbnailPosition = {padding,padding};
		// proccess pathname 'file' into a suitable format and store as char array
		char[] fileName = (fileNumber + ". " + fileHandler.isolateTitle(file)).toCharArray();
		// define the coordinates in the canvas to write the file name
		int[] namePosition = {2 ,size + 1 + 2*padding};
		// proccess fileNumber into a suitable format and store as char array
		char[] fileSize = (fileHandler.getSize(file) + " Bytes").toCharArray();
		// define the coordinates in the canvas to write the file size
		int[] sizePosition = {4 ,size + String.valueOf(fileNumber).length() + 3 + 2*padding};
		// for every point in thumbnail
		for (int i=0; i < thumbnail.length; i++){
			for (int j=0; j < thumbnail[i].length; j++){
				// if the point is not outside the canvas
				if (thumbnailPosition[0]+i < canvas.length && thumbnailPosition[1]+j < canvas[i].length){
					// write the thumbnail character at i j to the relative position in the canvas from the thumbnail coordinates
					canvas[thumbnailPosition[0]+i][thumbnailPosition[1]+j] = thumbnail[i][j];
				}
			}
		}
		// for every character in fileName
		for (int i=0; i < fileName.length; i++){
			// if the point is not outside the canvas
			if (namePosition[1]+i < canvas[namePosition[0]].length){
				// write the point in the fileName to the relative coordinate in the canvas from the name coordinates
				canvas[namePosition[0]][namePosition[1]+i] = fileName[i];
			}
		}
		// for every character in fileSize
		for (int i=0; i < fileSize.length; i++){
			// if the point is not outside the canvas
			if (sizePosition[1]+i < canvas[namePosition[0]].length){
				// write the point in the fileSize to the relative coordinate in the canvas from the size coordinates
				canvas[sizePosition[0]][sizePosition[1]+i] = fileSize[i];
			}
		}
		// return the canvas array
		return canvas;
	}
	/** <p>Create thumbnail for a given file</p>
	 * @param file Pathname of the file that is to be thumbnailed
	 * @param size The height and width of the thumbnail
	 * @return 2 Dimensional char array containing the generated thumbnail for the file at pathname 'file'
	 * */
	public char[][] makeThumbnail(String file, int size){
		// declare and intialise a 2D array with the dimensions size,size 
		char[][] thumbnail = new char[size][size];
		// create a 2D array with dimensions equal to the buffer
		// acts as a virtual buffer for storing the processed file
		char[][] fileBuffer = new char[Height][Width];
		// read the file into the fileBuffer
		fileHandler.readInCompressed(fileBuffer,file,0);
		// calculate the region size by which the fileBuffer is to be split
		int regionSize = fileBuffer.length/size;
		// for the dimensions of the thumbnail
		for (int i=0; i < thumbnail.length; i++){
			for (int j=0; j < thumbnail[i].length; j++){
				// create empty StringBuffer object
				StringBuffer region = new StringBuffer("");
				// for a square region of size regionSize
				for (int k=0; k < regionSize; k++){
					for (int l=0; l < regionSize; l++){
						// append the character at the relative coordinates in the fileBuffer to the StringBuffer
						region.append(fileBuffer[(i*regionSize)+k][(j*regionSize)+l]);
					}
				}
				// convert the StringBuffer to string and then from string to char array
				char[] characters = region.toString().toCharArray();
				// find the most frequent charater in the char array and write the returned value to the relative coordinate in the thumbnail
				thumbnail[i][j] = frequentCharacter(characters);
			}
		}
		// return the thumbnail char array
		return thumbnail;
	}
	/** <p>Find the most frequent character in an array of charaters</p>
	 * @param array Array to be searched for the most frequent character
	 * @return Character that appears most frequently in a char array
	 * @see <a href="https://www.educative.io/edpresso/how-to-find-the-most-frequent-word-in-an-array-of-strings">Code adapted from</a>
	 * */
	public char frequentCharacter(char[] array){
		// create new hashmap
		Map<Character,Integer> hshmap = new HashMap<Character,Integer>();
		// for every char in array
		for (char character : array){
			// if the character has already been added to the hashmap
			if (hshmap.keySet().contains(character)){
				// increase the count of the character by 1
				hshmap.put(character, hshmap.get(character) + 1);
			// if the character is novel to the hashmap
			}else{
				// add the character and set the count to 1
				hshmap.put(character, 1);
			}
		}
		// initialise max variables
		char maxChar = ' ';
		int maxVal = 0; 
		// for every entry in the hasmap
		for (Map.Entry<Character,Integer> entry : hshmap.entrySet()){ 
			// get the character 
			char key = entry.getKey(); 
			// get the count number of the character
			Integer count = entry.getValue(); 
			// if the count exceeds the maxVal
			if (count > maxVal){ 
				// set the max variables to the current entry values
				maxVal = count;
				maxChar = key;
			}
		}
		// return the char with the highest count
		return maxChar;
	}
}
