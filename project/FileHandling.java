import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.StringBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;
public class FileHandling{
	// the threshold represents the maximum size that the compression will use 1,1 compression before attempting an exhaustive compression scan
	private int threshold;
	// the yRange is the maximum vertical region size the compression scan will try
	private int yRange;
	// the xRange is the maximum horizontal region size the compression scan will try
	private int xRange;
	// compressionGraph is a list used to store values to be printed in the graph of the compression scan
	private List<int[]> compressionGraph = new ArrayList<>();
	/** <p>initialise the threshold and range values</p>
	 * @param th Value to set threshold to
	 * @param yr Value to set yRange to
	 * @param xr Value to set xRange to
	 * */
	public FileHandling(int th, int yr, int xr){
		threshold = th;
		yRange = yr;
		xRange = xr;
	}
	/** <p>Get the names of all files in a directory</p>
	 * @param directory Directory containing saved files
	 * @return Array of filenames 
	 * */
	public String[] getFiles(String directory){
		// declare the array files
		String[] files;
		// try to get a list of files
		try{
			// create file object with directory provided
			File folder = new File(directory);
			// use .list() method on File object to get array of names of files
			files = folder.list();
			// use Arrays.sort() to sort the names alphabetically
			Arrays.sort(files);
		// if exception thrown warn there are not files in the directory and set the files array as a new array with one item
		}catch (NullPointerException e){
			System.out.println("error: no files in directory: " + directory);
			files = new String[1];
		}
		// return array
		return files;
	}
	/** <p>Isolate the file name in a full file name</p>
	 * @param fullName Path of file
	 * @return The file name at the path
	 * */
	public String isolateTitle(String fullName){
		// split the fullname by / and store as array
		String[] parts = fullName.split("/");
		// get the last string in array
		String fileTitle = parts[parts.length-1];
		// return string
		return fileTitle;
	}
	/** <p>Get an estimate of the size of a plain text assuming that one character in the file contains one byte of information</p>
	 * @param file File to get size of
	 * @return The size of the file in bytes assuming each character in the file contains one byte of information
	 * */
	public int getSize(String file){
		// try to read the file
		try{
			// read the file at 'file'
			BufferedReader br = new BufferedReader(new FileReader(file));
			// store the result of the BufferedReader readLine() method as line
			String line = br.readLine();
			// get the .length() of the string
			int size = line.length();
			// return the size
			return size;
		// if error occurs warn and return 0
		}catch(Exception e){
			System.out.println("error: Problem reading file: '" + file + "'");
			//e.printStackTrace();
			return 0;
		}
	}
	/** <p>Randomly select a list pattern to load into the buffer with the list</p>
	 * @param buffer Reference to the array to load files into
	 * */
	public void displayList(char[][] buffer){
		// get a list of the files at "lists/patterns"
		String[] fileList = getFiles("lists/patterns/");
		// get the file at a random valid position in the file list
		String selectedFile = fileList[(int)(Math.random() * fileList.length)];
		// read in the list file with mode 0 (overwrite)
		readInCompressed(buffer,"lists/list.csv",0);
		// read in the selected pattern file with mode 1 (merge)
		readInCompressed(buffer,"lists/patterns/" + selectedFile,1);
	}
	/** <p>Decompress saved files and write them into the buffer</p>
	 * @param buffer Reference to the array to load files into
	 * @param file Path of the file to read
	 * @param mode Accepts mode 0/1 as overwrite/merge
	 * */
	public void readInCompressed(char[][] buffer, String file, int mode){
		try{
			//long start = System.nanoTime();
			//
			// read the file at 'file'
			BufferedReader br = new BufferedReader(new FileReader(file));
			// store the result of the BufferedReader readLine() method as line
			String line = br.readLine();
			// split the contents of the file into an array of Strings using , as the delimiter
			String[] values = line.split(",");
			// declare and initialise an int array with length 2. This will store the header values needed to decode the file
			int[] header = new int[2];
			// create empty StringBuffer object
			StringBuffer decompressedBuffer = new StringBuffer("");
			// for every item in the array
			for (int i = 0; i < values.length; i++){
				// if the item is the first item
				if (i == 0){
					// split item by grave character into headerParts
					String[] headerParts = values[0].split("`");
					// for every header part
					for (int j=0; j < headerParts.length; j++){
						// parse each part as int and set header value
						header[j] = Integer.parseInt(headerParts[j]);
					}
				// for every other item
				}else{
					// split the item into its parts delimited by grave
					String[] parts = values[i].split("`");
					// parse the first part as an integer to determine the number of repeats for the region
					int repeat = Integer.parseInt(parts[0]);
					// store the second part in new variable representing the contents of the region
					String characters = parts[1];
					// for the number of repeats determined
					for (int j=0; j < repeat; j++){
						// append the region characters to the decompressed buffer
						decompressedBuffer.append(characters).append(",");
					}
				}
			}
			// split the decompressed buffer as a string into an array where each item is delimited by comma
			String[] expandedValues = decompressedBuffer.toString().split(",");
			// calculate the number of vertical and horizontal regions possible with the header y and x values
			final int verticalRegions = (int)Math.ceil((double)buffer.length/header[0]);
			final int horizontalRegions = (int)Math.ceil((double)buffer[0].length/header[1]);
			// for every region in the buffer
			for (int index=0; index < verticalRegions*horizontalRegions; index++){
				// convert the index of the region into a relative coordinate of unit region-size-y/x
				int[] relativePosition = {
					(int)Math.floor((double)index/horizontalRegions),
					index % horizontalRegions
				};
				// calculate the real dimensions of the region at the current index in the buffer
				int[] regionDimensions = regionDimensions(buffer,header[0],header[1],index);
				// convert the region at the current index to a char array
				char[] characters = expandedValues[index].toCharArray();
				// for every point within the region dimensions 
				for (int i=0; i < regionDimensions[0]; i++){
					for (int j=0; j < regionDimensions[1]; j++){
						// if the mode is 1 then write unless character is a space and if mode is 0 always write
						if ((characters[i*(characters.length/header[0])+j] != ' ' && mode == 1) || (mode == 0)){
							// write to the character at distance i and j from the origin of the region at relativePosition(y/x)*header(y/x) in the buffer
							buffer[(relativePosition[0]*header[0])+i][(relativePosition[1]*header[1])+j] = characters[
								// character to write determined relative to dimensions available
								i*(characters.length/header[0])+j
							];
						}
					}
				}
				/*DEBUG*///Thread.sleep(50);
				/*DEBUG*///new Shapes().print(buffer);
			}
			/*long stop = System.nanoTime();
			double time = (double) (stop-start)/1_000_000;
			System.out.println("Loaded in " + time + " milliseconds; " + file);*/
		// if any exception is thrown, warn and print stack trace describing problem
		}catch(Exception e){
			System.out.println("error: Problem loading file: '" + file + "'");
			e.printStackTrace();
		}
	}
	/** <p>Interface method for saving files using appropriate region dimensions</p>
	 * @param buffer Reference to the array to compress
	 * @param file Path of the file to read
	 * */
	public void writeOutCompressed(char[][] buffer, String file){
		// store the system time in nanoseconds at beggining of timed period
		long start = System.nanoTime();
		// compress the buffer with 1x1 region dimensions
		String data = compressBuffer(buffer,1,1);
		// if the resulting size of the compression is greater than the threshold
		if (data.length() > threshold){
			System.out.println("Compressing...");
			// set the variable date to the returned value of the compression scan
			String smallest = compressionScan(buffer);
			data = smallest;
		}
		// store the system time in nanoseconds at end of timed period
		long stop = System.nanoTime();
		// calculate difference in time at begging and end of timed period
		double time = (double) (stop-start)/1_000_000_000;
		// print the compressed buffer
		System.out.println("\n"+data);
		// print the length of the compressed data
		System.out.println(data.length() + " Bytes");
		// print the time taken to perform operation
		System.out.print("\nCompleted in " + time + " seconds\nWriting to file..."); // ~9.2 seconds +-0.2
		// attempt to write to file
		try{
			// create FileWriter object with file
			FileWriter writer = new FileWriter(file);
			// append the data to file
			writer.append(data);
			// close the writer
			writer.close();
			// announce file is written
			System.out.print(" Done.\n\n");
		// if exception is thrown, warn and print stack trace
		}catch(Exception e){
			System.out.println("error: Problem writing file: '" + file + "'");
			e.printStackTrace();
		}
	}
	/** <p>Method for finding the optimal region dimensions to compress the buffer with</p>
	 * @param buffer Reference to the array to compress
	 * @return The optimal compressed form of the buffer
	 * */
	public String compressionScan(char[][] buffer){
		// store the system time in nanoseconds at beggining of timed period
		long start = System.nanoTime();
		/* Before region overlay comparison was used as the main method of comparing the regions in the compression stage, 
		 * a shortcut was possible where the optimal region dimensions could be found by finding the smallest compression 
		 * size along the first row of the buffer and then checking only the dimensions along the y axis at this smallest 
		 * x coordinate.
		 * 
		 * This occurred because regions at the edge of the buffer would have dimensions that overlapped the edge of the
		 * buffer dimensions and therefore would always differ form the previous region. Because of this, the graph of 
		 * each row in the compression map contained the same information where the features of the graph scaled 
		 * proportionally to each other, allowing the scan to skip values that did not have the x dimension of the 
		 * smallest value in the first row.
		 * 
		 * This shortcut was no longer completely effective with region overlay comparison for files with certain 
		 * abnormal features. Features such as large amounts of blank space in a tiled region, which had optimal region
		 * dimensions that where skipped in the scan because they did not have x dimensions that matched the optimal x 
		 * dimension along the first row.
		 * 
		 * However this shortcut is still effective in most scenarios and does well at reducing 
		 * compression time as it performs 50 times fewer checks in the scan than a full 100x100 exhaustive map would.
		 * */
		/*
		// initialise minimum variables for the x check
		int minXSize = 0;
		int minXCoord = 0;
		// create array of string for every x dimension within the range
		String[] xSection = 
			// create intStream of number between 1 and x range + 1 and map each number to
			IntStream.range(1, xRange + 1).mapToObj(j -> 
				// the string returned by each compression performed with given dimensions
				compressBuffer(buffer,1,j)
			// perform operations in parallel
			).parallel()
			// collect returned strings as string array
			.toArray(String[]::new)
		;
		// for every item in the string array
		for (int i=0; i < xSection.length; i++){
			// if the length of the string at a position is less than the minimun value stored
			// or otherwise if the minXSize is 0 assume no min value has been found yet
			if (xSection[i].length() < minXSize || minXSize == 0){
				// set the minXSize value to the size of the string which has met the condition
				minXSize = xSection[i].length();
				// store the dimension of the min x found
				minXCoord = i;
			}
			// add item to graph
			addGraphItem(0,i,xSection[i].length());
		}
		// adding item with size 0 creates break point in graph
		addGraphItem(0,0,0);
		// initialise minimum variables for the y check
		int minYSize = 0;
		int minYCoord = 0;
		// initialise variable represnting the column to check in the y check
		final int column = minXCoord+1;
		// create array of string for every y dimension within the range
		String[] ySection = 
			// create intStream of number between 1 and y range + 1 and map each number to
			IntStream.range(1, yRange + 1).mapToObj(i -> 
				// the string returned by each compression performed with given dimensions
				compressBuffer(buffer,i,column)
			// perform operations in parallel
			).parallel()
			// collect returned strings as string array
			.toArray(String[]::new)
		;
		// for every item in the string array
		for (int i=0; i < ySection.length; i++){
			// if the length of the string at a position is less than the minimun value stored
			// or otherwise if the minYSize is 0 assume no min value has been found yet
			if (ySection[i].length() < minYSize || minYSize == 0){
				// set the minYSize value to the size of the string which has met the condition
				minYSize = ySection[i].length();
				// store the dimension of the min y found
				minYCoord = i;
			}
			// add item to graph
			addGraphItem(i,minXCoord,ySection[i].length());
		}
		// print the graph
		printGraph(50,5);
		// store the system time in nanoseconds at end of timed period
		long stop = System.nanoTime();
		// calculate difference in time at begging and end of timed period
		double time = (double) (stop-start)/1_000_000_000;
		// print information about the scan including the number of compressions performed and the time taken to perform those compressions
		System.out.println("\nCompleted " + (ySection.length+xSection.length) + " parallel checks in " + time + " seconds\n");
		System.out.print("END\n");
		// return the optimal string
		return ySection[minYCoord];
		*/
		//
		// create a 2Dimensional array of string in parallel using streams called the compressionMap
		String[][] compressionMap = 
			// create intstream of number between range of 1 and the y range + 1 and map each number to
			IntStream.range(1, yRange + 1).mapToObj(i ->
				// another intstream of numbers between the range of 1 and x range + 1 where each value is mapped to
				IntStream.range(1, xRange + 1).mapToObj(j -> 
					// the compressBuffer method with the int values i and j passed as dimensions to compress with
					compressBuffer(buffer,i,j)
				// perform operations in parallel
				).parallel()
				// collect returned strings as a string array
				.toArray(String[]::new)
			// perform operations in parallel
			).parallel()
			// collect returned arrays as a 2D String array
			.toArray(String[][]::new)
		;
		// initialise min variables minSize and smallest[] representing the minimum length of the compressions attempted and the dimensions at which the minsize occurs respectively
		int minSize = 0;
		int[] smallest = new int[2];
		// for every item in the compressonMap
		for (int i=0; i < compressionMap.length; i++){
			for (int j=0; j < compressionMap[i].length; j++){
				// compare the length of the string at the current position to the minimum size and if it is less than the minSize then set the minSize accordingly.
				// alternatively if the minSize is 0 then it is assumed that the item is the first in the compressionMap and therefore is set as the initial minimum size.
				if (compressionMap[i][j].length() <= minSize || minSize == 0){
					// set minSize to the length of the item at i j
					minSize = compressionMap[i][j].length();
					// set the values of the smallest dimensions at which this minSize is found
					smallest[0] = i;
					smallest[1] = j;
					// add the values to the compression graph
					addGraphItem(i,j,compressionMap[i][j].length());
				}
			}
		}
		// print the compression graph once the search completes
		printGraph(50,5);
		// store the system time in nanoseconds at end of timed period
		long stop = System.nanoTime();
		// calculate difference in time at begging and end of timed period
		double time = (double) (stop-start)/1_000_000_000;
		// print information about the scan including the number of compressions performed and the time taken to perform those compressions
		System.out.println("\nCompleted " + (compressionMap.length*compressionMap[0].length) + " parallel checks in " + time + " seconds\n");
		System.out.print("END\n");
		// return the string found at the smallest-dimensions in the compression map
		return compressionMap[smallest[0]][smallest[1]];
	}
	/** <p>Method for converting the data stored in the buffer into a string containg a literally smaller amount of data</p>
	 * <p>The method iterates through the buffer converting areas of the buffer refered to as regions into comparable strings. Adjacent regions that 
	 * are determined equal by region overlay comparison (ROC-equal) are counted. The equal regions can therefore be represented by a repeat of the 
	 * region containing the most data.</p>
	 * @param buffer Reference to the array to read
	 * @param height Height of the region size 
	 * @param width Width of the region size 
	 * @return The compressed form of the buffer with supplied height and width dimensions
	 * */
	public String compressBuffer(char[][] buffer, int height, int width){
		// calculate vertical and horizontal regions that fit into buffer dimensions
		final int verticalRegions = (int)Math.ceil((double)buffer.length/height);
		final int horizontalRegions = (int)Math.ceil((double)buffer[0].length/width);
		// create array of comparable strings where each item represents a region in the buffer
		String[] formattedBuffer =
			// create intsream of number between 0 and the total number of regions in the buffer and map each number to the contained operation
			IntStream.range(0, verticalRegions * horizontalRegions).mapToObj(index -> {
				// (contained operation)
				// create empty stringBuffer object
				StringBuffer region = new StringBuffer("");
				// convert the index value of the region into a relative coorinate of unit region-size-y/x
				int[] relativePosition = {
					(int)Math.floor((double)index/horizontalRegions),
					index % horizontalRegions
				};
				// for the dimensions of the region height x width
				for (int i=0; i < height; i++){
					for (int j=0; j < width; j++){
						// if the coordinate to be appended lies within the buffer dimensions
						if ((relativePosition[0]*height)+i < buffer.length && (relativePosition[1]*width)+j < buffer[i].length){
							// append the relative character to the stringBuffer
							region.append(buffer[(relativePosition[0]*height)+i][(relativePosition[1]*width)+j]);
						}
					}
				}
				// return the stringBuffer region as string
				return region.toString();
			// perform operations in parallel
			}).parallel()
			// collect returned strings as a string array
			.toArray(String[]::new)
		;
		// initialise header string in correct format
		String header = height + "`" + width + ",";
		// create stringBuffer object with header string
		StringBuffer compressedBuffer = new StringBuffer(header);
		// count variable represents the number of adjacent occurrences of a region
		int count = 1;
		/* The referenceDataIndex is the index of the region that is compared in the regon overlay comparison.
		 * It is set as the region containing the most amount of data. If when iterating through the regions are not compared to the region at this 
		 * index then corruption occurs in the output.
		 * */
		int refenceDataIndex = 0;
		// for every item in the formatted buffer
		for (int index=0; index < formattedBuffer.length; index++){
			// if the next index is not beyond the range of the array
			if (index + 1 < formattedBuffer.length){
				// evaluate whether the region at index+1 is ROC-equal to the region at the referenceDataIndex
				if (regionOverlayComparison(buffer,height,width,refenceDataIndex,formattedBuffer[refenceDataIndex],index+1,formattedBuffer[index+1])){
					// if the regions are ROC-equal then count up then number of repeats and if the region at index+1 contains more information than the region at the referenceDataIndex then set the referenceDataIndex to index+1
					if (formattedBuffer[index+1].length() > formattedBuffer[refenceDataIndex].length()) refenceDataIndex = index+1;
					count++;
				}else{
					// if the next region is not ROC-equal then append the value of the number of repeats and the region found at the refenceDataIndex to the StringBuffer in the correct format
					compressedBuffer.append(count).append("`").append(formattedBuffer[refenceDataIndex]).append(",");
					// set the referenceDataIndex to the next index
					refenceDataIndex = index+1;
					// return the count value to 1
					count = 1;
				}
			// if the region at index+1 is beyond the range of the array, then the end of the array has been reached.
			// no further action can take place so write the current number of repeats and the region at the referenceDataIndex to the StringBuffer in the correct format
			}else{
				compressedBuffer.append(count).append("`").append(formattedBuffer[refenceDataIndex]).append(",");
			}
		}
		//System.out.print(height + "x" + width + "	");
		//
		//return the StringBuffer as a string
		return compressedBuffer.toString();
	}
	/** <p>Evaluation condition for regions in compression method</p>
	 * @param buffer Reference to the array to read
	 * @param height Height of the region size 
	 * @param height Width of the region size 
	 * @param referenceDataIndex Index of the region that contains the most information in the current count
	 * @param referenceData Region found at the referenceDataIndex 
	 * @param nextDataIndex Index of the region to be compared against the region at the refernceDataIndex
	 * @param nextData Region found at the nextDataIndex
	 * @return True or False result
	 * */
	private boolean regionOverlayComparison(char[][] buffer,int height, int width, int refernceDataIndex, String referenceData, int nextDataIndex, String nextData){
		// calculate the real dimensions of the regions at their respective indexes in the buffer
		int[] referenceDimensions = regionDimensions(buffer,height,width,refernceDataIndex);
		int[] nextDimensions = regionDimensions(buffer,height,width,nextDataIndex);
		// initialise result variable
		boolean result = true;
		// for the smallest of the two dimensions
		for (int i=0; i < referenceDimensions[0] && i < nextDimensions[0]; i++){
			for (int j=0; j < referenceDimensions[1] && j < nextDimensions[1]; j++){
				// if the characters at the relative coordinates of the two regions are not the same then set result to false and break loop
				if (nextData.charAt((i*nextDimensions[1]) + j) != referenceData.charAt((i*referenceDimensions[1]) + j)){
					result = false;
					break;
				}
			}
		}
		// debug outputs readable representation of comparison
		/*DEBUG*///System.out.println(nextDataIndex-1 + " " + result + " [" + referenceData + "] ? [" + nextData + "]");
		// return boolean
		return result;
	}
	/** <p>Calculate real dimensions of the region at the index in the buffer</p>
	 * @param buffer Reference to the array to read
	 * @param height Height of the region size 
	 * @param height Width of the region size 
	 * @param index Index of the region with unknown dimensions
	 * @return Int array containing dimesions of region as y x
	 * */
	private int[] regionDimensions(char[][] buffer,int height, int width, int index){
		// declare variables to be used
		int ruler, relativeWidth, relativeHeight;
		// calcualte the number of horizontal regions that fit into the buffer dimensions
		int horizontalRegions = (int)Math.ceil((double)buffer[0].length/width);
		// convert the index into a relative coordinate of unit region-size-y/x
		int[] relativeRegion = {
			(int)Math.floor((double)index/horizontalRegions),
			index % horizontalRegions
		};
		// set the variable ruler to the relative height coordinate in the buffer
		ruler = relativeRegion[0] * height;
		// count up until the ruler reaches either the beggining of the next region of the edge of the buffer
		while (ruler < (relativeRegion[0] + 1) * height && ruler < buffer.length){
			ruler++;
		}
		// calculate difference between start and end points of the ruler and store result as relativeHeight
		relativeHeight = ruler - (relativeRegion[0] * height);
		// set the variable ruler to the relative width coordinate in the buffer
		ruler = relativeRegion[1] * width;
		// count up until the ruler reaches either the beggining of the next region of the edge of the buffer
		while (ruler < (relativeRegion[1] + 1) * width && ruler < buffer[0].length){
			ruler++;
		}
		// calculate difference between start and end points of the ruler and store result as relativeWidth
		relativeWidth = ruler - (relativeRegion[1] * width);
		// convert the calculated dimension in the correct format in an int[] array
		int[] dimensions = {relativeHeight,relativeWidth};
		// return int[] array
		return dimensions;
	}
	/** <p>Prints the graph of the items added to the compression graph</p>
	 * @param size Width of the graph in characters to fit the data onto
	 * @param labels Number of labels along the axis
	 * */
	public void printGraph(int size, int labels){
		// determine scale
		int scale = 0;
		// for every item in the compressionGraph
		for (int i=0; i < compressionGraph.size(); i++){
			// get the int array represnting a data point in the graph
			int[] item = compressionGraph.get(i);
			// if the size of the item is greater than any other set the scale to this size
			if (item[2] > scale){
				scale = item[2];
			}
		}
		/* The axis formats poorly when the scale of the graph is very 
		 * small. A way around this is to decrease the number of labels 
		 * to a value that the scale is more conveniently divisible by
		 * such as the largest label that will appear given the scale.
		 * */
		// calculate the largest number of labels
		int largestLabel = (int)Math.ceil((double)scale/1000);
		// if the largest number of labels is less than the labels variable set then set the labels variable to the largest number of labels
		if (largestLabel - labels < 0) labels = largestLabel;
		// print two new lines
		System.out.print("\n\n");
		// for every item in the graph
		for (int i=0; i < compressionGraph.size(); i++){
			// get the array representing the item
			int item[] = compressionGraph.get(i);
			// if the item size is not 0
			if (item[2] != 0){
				// create empty stringBuffer object
				StringBuffer graphItem = new StringBuffer("");
				// append the values stored in the item as size : height x width TAB to the stringBuffer
				graphItem.append(item[2]).append(":").append(item[0]+1).append("x").append(item[1]+1).append("	");
				// for the size-value scaled onto the graph-size
				for (int n = 0; n < (int)Math.round(item[2]*((double)size/scale)); n++){
					// if the point lies at the same point as a label
					if (n % (size/labels) == 0){
						// append a : character
						graphItem.append(":");
					// otherwise append a | character
					}else{
						graphItem.append("|");
					}
				}
				// print the formatted data
				System.out.println(graphItem.toString());
			// otherwise print an asterism representing a break in the data
			}else{
				System.out.println("***");
			}
		}
		// creating the axis
		//create stringBuffer with TAB TAB as spacing
		StringBuffer graphAxis = new StringBuffer("		");
		// for the number of labels
		for (int i=0; i <= labels; i++){
			// create the label
			String label = "^" + (int)Math.ceil((double)((i)*scale)/(labels*1000));
			// append the label
			graphAxis.append(label);
			// for the amount of space left in between the label and the next label, whilst the label isn't the final one
			for (int j=0; j < (size/labels) - label.length() &&  i < labels; j++){
				// append -
				graphAxis.append("-");
			}
		}
		// print the formatted axis
		System.out.println(graphAxis.toString());
		// clear the compressionGraph
		compressionGraph.clear();
	}
	/** <p>Adds correctly formatted item to the compression graph</p>
	 * @param y Region height
	 * @param x Region Width
	 * @param size Size of the compression with region dimensions y x
	 * */
	public void addGraphItem(int y, int x, int size){
		// create array with y x and size
		int[] item = {y,x,size};
		// add the array to the compressionGraph
		compressionGraph.add(item);
	}
}
