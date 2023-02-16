import java.util.Scanner;
public class Shapes{
	// Variable characterDimensionsRatioHW is used to stretch the width of the screen when a square width font is not available in the terminal. The value is a representation of the character height / character width in the font.
	private int characterDimensionsRatioHW = 1;
	// The size and dimensions[] variables dictate the number of characters that make up the width and height of the buffer and therefore the total number of possible coordinates to be manipulated.
	private int size = 100;
	private int[] dimensions = {size,size};
	// Buffer WorkBuffer and Filemenu are Display subclasses, each containing their own buffer that their own methods can write to. The workbuffer is necessary to provide a temporary surface on which characters can be written such as the cursor, which is expected to behave seperatly to the other character drawring methods. The FileMenu subclass contains methods for thumbnailing and drawring the filemenu.
	private Buffer buffer = new Buffer(dimensions[0],dimensions[1]);
	private WorkBuffer workBuffer = new WorkBuffer(dimensions[0],dimensions[1]);
	private FileMenu fileMenu = new FileMenu(dimensions[0],dimensions[1],20,2,1);
	// FileHandling contains methods for file compression/decompression, as well as writing to disk.
	private FileHandling fileHandler = new FileHandling(12,dimensions[0],dimensions[1]);
	// Global scanner object used between methods in class.
	private Scanner scanner = new Scanner(System.in);
	// booleans selecting and running are used to maintain/exit certain control loops.
	private boolean selecting = true;
	private boolean running = true;
	// Main function
	public static void main(String[] args){
		// begin control.
		new Shapes().control();
	}
	/** <p>Populates main buffer with list file and list pattern file (main screen) and records the original state of the buffer for the history function.</p>
	 * */
	public void prepareDisplay(){
		fileHandler.displayList(buffer.returnBuffer());
		// recordBuffer copies a complete version of the main buffer to memory for use in the buffer history.
		buffer.recordBuffer();
	}
	/** <p>Main control loop</p>
	 * */
	public void control(){
		prepareDisplay();
		// Main control loop, runs until user inputs a command to set running to false.
		while (running){
			// The workBuffer is 'initialised' meaning the buffer is copied into the workbuffer so that temporary changes can be displayed such as the cursor or an unconfirmed drawring.
			initialiseWork();
			// Cursor is drawn on the workBuffer
			workBuffer.drawCursor("+");
			// The print function gets passed a reference to the workBuffer buffer array, and prints each character stored in the array to System.out
			print(workBuffer.returnBuffer());
			// Progress is paused by scanner until user submits an input.
			System.out.print("\nInput:");
			String input = scanner.nextLine();
			// Once the input is submitted, it is compared in a very large if else statement to determine what action to take. These functions and commands may be better stored in a hashmap.
			if (input.equals("1")){
				placeShadedCircle();
			}else if (input.equals("2")){
				placeCircle();
			}else if (input.equals("3")){
				placeLine();
			}else if (input.equals("4")){
				placeCurve();
			}else if (input.equals("5")){
				placePhaseCircle();
			}else if (input.equals("6")){
				placeRectangle();
			}else if (input.equals("7")){
				placeCross();
			}else if (input.equals("8")){
				placeOval();
			}else if (input.equals("9")){
				placeFreehand();
			}else if (input.equals("0")){
				placeEraser();
			}else if(input.equals("~")){
				placeMandelbrot();
			}
			else if (input.equals("r")){
				// initialise workBuffer
				initialiseWork();
				// perform rotation on workBuffer
				workBuffer.rotate(0);
				// commit work to buffer
				commitWork();
			}else if (input.equals("R")){
				// initialise workBuffer
				initialiseWork();
				// perform rotation on workBuffer
				workBuffer.rotate(1);
				// commit work to buffer
				commitWork();
			}else if (input.equals("m")){
				// initialise workBuffer
				initialiseWork();
				// perform mirror on workBuffer
				workBuffer.mirror(0);
				// commit work to buffer
				commitWork();
			}else if (input.equals("M")){
				// initialise workBuffer
				initialiseWork();
				// perform mirror on workBuffer
				workBuffer.mirror(1);
				// commit work to buffer
				commitWork();
			}else if (input.equals("x")){
				placeCut();
			}else if (input.equals("X")){
				// initialise workBuffer
				initialiseWork();
				// copy item from workBuffer
				workBuffer.copyItem(1);
				// erase item from workBuffer
				workBuffer.eraseItem(1);
				// commit work to buffer
				commitWork();
			}else if (input.equals("c")){
				placeCopy();
			}else if (input.equals("C")){
				// initialise workBuffer
				initialiseWork();
				// copy item from buffer
				workBuffer.copyItem(1);
				// commit work to buffer
				commitWork();
			}else if (input.equals("v")){
				placePaste(0);
			}else if (input.equals("V")){
				placePaste(1);
			}else if (input.equals("f")){
				placeFill();
			}else if (input.equals("F")){
				placeShadedFill();
			}else if (input.equals("z")){
				buffer.stepBack();
			}else if (input.equals("Z")){
				buffer.stepForward();
			}else if (input.equals("t")){
				// initialise workBuffer
				initialiseWork();
				// perform tile on workBuffer
				workBuffer.tile(0);
				// commit work to buffer
				commitWork();
			}else if (input.equals("T")){
				// initialise workBuffer
				initialiseWork();
				// perform tile on workBuffer
				workBuffer.tile(1);
				// commit work to buffer
				commitWork();
			}else if (input.equals("list")){
				prepareDisplay();
			}else if (input.equals("clear")){
				// fill the main buffer with space characters
				buffer.initialise();
				// record state of buffer into history
				buffer.recordBuffer();
			}else if (input.equals("files")){
				controlFileMenu("saved/");
			}else if (input.equals("save")){
				// compress contents of buffer and save to savedBuffer.csv
				fileHandler.writeOutCompressed(buffer.returnBuffer(),"savedBuffer.csv");
				// record state of buffer into history
				buffer.recordBuffer();
			}else if (input.equals("load")){
				// load savedBuffer.csv into buffer
				fileHandler.readInCompressed(buffer.returnBuffer(),"savedBuffer.csv",0);
				// record state of buffer into history
				buffer.recordBuffer();
			}else if (input.equals("merge")){
				// load savedBuffer.csv into buffer
				fileHandler.readInCompressed(buffer.returnBuffer(),"savedBuffer.csv",1);
				// record state of buffer into history
				buffer.recordBuffer();
			}else if (input.equals("exit")){
				// set main loop condition to false, end program
				running = false;
			}else{
				// If none of these command are supplied then the input is checked with the positionCurosr method which is able to alter the coordinates of the cursor in the screen.
				workBuffer.positionCursor(input);
			}
			// At the end of this loop, checkForBoundary makes sure that the cursor lies on a coordinate that is within the bounds of the buffer, creating a hard edge for the cursor.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing shaded circle</p>
	 * */
	 // The place methods each contain a control loop controlled by the boolean variable 'selecting'
	public void placeShadedCircle(){
		// In this place method, it is necessary for the circle calculation to know the coordinate at which the loop was started, this coordinate is stored as the origin in workbuffer using the setOrigin method. 
		workBuffer.setOrigin();
		// Before the loop the boolean selecting is set true to make sure the loop can continue when reached.
		selecting = true;
		while (selecting){
			// The buffer is copied into the workBuffer
			initialiseWork();
			// The cursor is drawn.
			workBuffer.drawCursor("+");
			// A shaded circle is drawn on the workBuffer.
			workBuffer.drawShadedCircle('=','-','.');
			// The workBuffer is printed.
			print(workBuffer.returnBuffer());
			// Loop pauses for user input.
			System.out.print("\nDrawing shaded circle, r:" + workBuffer.returnDistance() + "\nInput:");
			String selection = scanner.nextLine();
			// Input is compared to certain strings.
			if (selection.equals("")){
				// If input is empty then the drawring is 'confirmed' and the workBuffer is initialised again, to remove any temporary characters such as the cursor.
				initialiseWork();
				// A shaded circle is drawn onto the workBuffer
				workBuffer.drawShadedCircle('=','-','.');
				// The workBuffer is copied into the buffer, and the boolean selecting is set false, exiting the loop.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// An input of 'q' sets the boolean selecting false without further action, discarding any changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing circle</p>
	 * */
	public void placeCircle(){
		// Origin coordinate is set.
		workBuffer.setOrigin();
		// Loop condition is prepared and loop is begun.
		selecting = true;
		while (selecting){
			// workBuffer is initialised.
			initialiseWork();
			// Cursor is drawn in workBuffer.
			workBuffer.drawCursor("+");
			// Circle is drawn in WorkBuffer.
			workBuffer.drawCircle('_');
			// WorkBuffer is printed to System.out
			print(workBuffer.returnBuffer());
			// Loop pauses for user input.
			System.out.print("\nDrawing circle, r:" + workBuffer.returnDistance() + "\nInput:");
			String selection = scanner.nextLine();
			// Input is checked.
			if (selection.equals("")){
				// If confirmed then workBuffer is initialied.
				initialiseWork();
				// Circle is drawn in workBuffer.
				workBuffer.drawCircle('_');
				// WorkBuffer is copied into buffer and loop ends.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// boolean selecting set false without further action, discarding any changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing line</p>
	 * */
	public void placeLine(){
		// Origin not necassary for this shape, so is not set.
		//  loop condition prepared and loop started.
		selecting = true;
		while (selecting){
			// WorkBuffer is initialised
			initialiseWork();
			// A line is drawn at the coorindate the cursor lies.
			workBuffer.drawLine('*');
			// The workBuffer is printed.
			print(workBuffer.returnBuffer());
			// loop pauses for user input.
			System.out.print("\nDrawing line, gradient:" + workBuffer.returnGradient() + "\nInput:");
			String selection = scanner.nextLine();
			// input is checked.
			if (selection.equals("")){
				// if confirmed then the workBuffer is initialised.
				initialiseWork();
				// A line is drawn at the cursor coordinate with gradient m
				workBuffer.drawLine('*');
				// work is commited to buffer and loop exits.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// boolean selecting set false without further action, discarding any changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionLine(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing curve</p>
	 * */
	public void placeCurve(){
		// loop condition prepared and loop started.
		selecting = true;
		while (selecting){
			// workbuffer initialised.
			initialiseWork();
			// curve drawn in workBuffer
			workBuffer.drawCurve(2,'*');
			// WorkBuffer printed to System.out
			print(workBuffer.returnBuffer());
			// Variable used to draw the curve is returned so its values can be displayed to the user
			double[] stretch = workBuffer.returnMultiply();
			System.out.print("\nDrawing curve, x/y Multiplier:" + stretch[0] + "/" + stretch[1] + "\nInput:");
			// loop pauses to wait for input.
			String selection = scanner.nextLine();
			// input is checked.
			if (selection.equals("")){
				// workBuffer is initialised.
				initialiseWork();
				// Curve is drawn in workBuffer
				workBuffer.drawCurve(2,'*');
				// work is commited to buffer and loop condition is set false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCurve(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing phase circle</p>
	 * */
	public void placePhaseCircle(){
		// Origin coordinate is set
		workBuffer.setOrigin();
		// loop condition is prepared and stage 1 loop begins
		selecting = true;
		while (selecting){
			// workbuffer initialised
			initialiseWork();
			// the straight-line distance between the coordinate and origin is calculated and returned as r.
			double r = workBuffer.returnDistance();
			// Phase is drawn in workBuffer with radius r
			workBuffer.drawPhase(r,'=');
			// cursor is drawn to workBuffer.
			workBuffer.drawCursor("+");
			// WorkBuffer printed to System.out
			print(workBuffer.returnBuffer());
			System.out.print("\nDrawing phase circle [stage 1] r:" + workBuffer.returnDistance() + "\nInput:");
			// loop pauses and waits for user input.
			String selection = scanner.nextLine();
			// input is checked
			if (selection.equals("")){
				// if stage confirmed then second control loop begins using the radius determined in the first stage 
				while (selecting){
					//workBuffer initialised.
					initialiseWork();
					// Phase drawb in workBuffer.
					workBuffer.drawPhase(r,'=');
					// Cursor drawn in workBuffer
					workBuffer.drawCursor("+");
					// WorkBuffer printed to System.out
					print(workBuffer.returnBuffer());
					System.out.print("\nDrawing phase circle [stage 2] Distance:" + workBuffer.returnDistance() + "\nInput:");
					// loop pauses and waits for input
					String selectionB = scanner.nextLine();
					if (selectionB.equals("")){
						// workBuffer initialised
						initialiseWork();
						// Phase drawn in workBuffer
						workBuffer.drawPhase(r,'=');
						// workBuffer commited to buffer and loop condition set false.
						commitWork();
						selecting = false;
					}else if (selectionB.equals("q")){
						// loop condition set false with no further action, discarding changes made in the workBuffer.
						selecting = false;
					}else{
						// Any other input is handled by the positionCursor method.
						workBuffer.positionCursor(selectionB);
					}
					// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
				}
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing rectangle</p>
	 * */
	public void placeRectangle(){
		// origin coordinate is set
		workBuffer.setOrigin();
		// loop condition prepared and loop begins
		selecting = true;
		while (selecting){
			// workBuffer initialised
			initialiseWork();
			// rectangle drawn between origin and cursor on workbuffer.
			workBuffer.drawRectangle('-');
			// cursor drawn on workbuffer
			workBuffer.drawCursor("+");
			// workBuffer printed to system.out 
			print(workBuffer.returnBuffer());
			System.out.print("\nDrawing rectangle\nInput:");
			// loop pauses for user input
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// workBuffer initialised
				initialiseWork();
				// rectangle drawn to workBuffer
				workBuffer.drawRectangle('-');
				// workBuffer copied to buffer and loop condition set false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary creates hard edge for the cursor at the adge of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing cross</p>
	 * */
	public void placeCross(){
		// loop condition prepared and loop begins
		selecting = true;
		while (selecting){
			// workBuffer initialised
			initialiseWork();
			// cross drawn on workBuffer.
			workBuffer.drawCross('*');
			// workBuffer printed to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nDrawing cross\nInput:");
			// loop pauses for user input.
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// workBuffer initialised
				initialiseWork();
				// cross drawn in workBuffer
				workBuffer.drawCross('*');
				//work commited to buffer and loop condition set false/
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing oval</p>
	 * */
	public void placeOval(){
		// origin coordinate is set
		workBuffer.setOrigin();
		// loop condition is prepared and loop begins
		selecting = true;
		while (selecting){
			// workBuffer is initialised
			initialiseWork();
			// oval is drawn between the origin and the cursor on workBuffer
			workBuffer.drawOval(1.5,'*');
			// cursor is drawn on workBuffer
			workBuffer.drawCursor("+");
			//workBuffer is printed to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nDrawing oval\nInput:");
			// loop pauses for user input
			String selection = scanner.nextLine();
			if (selection.equals("")){
				//workBuffer is initialised
				initialiseWork();
				// oval is drawn between cursor and origin
				workBuffer.drawOval(1.5,'*');
				// work is commited to buffer and loop condition set false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// checkForBoundary is not called here because it is not neccasary for the cursor to stay within the bounds for drawring this shape.
		}
	}
	/** <p>Control loop for placing freehand</p>
	 * */
	public void placeFreehand(){
		// prepare loop condition.
		selecting = true;
		// ask user to input freehand brush
		System.out.print("\nFreehand brush:");
		// System pauses for user input
		String freehandString = scanner.nextLine();
		// check input for illegal characters (those that would interfere with the shape of the buffer such as tab or space or those that would break the compression and decompression used for saving files.)
		if (freehandString.contains(",") || freehandString.contains("`") || freehandString.contains("	") || freehandString.equals("")){
			// If illegal then issue warning and set loop condition false
			System.out.println("error: Cannot selected this brush, empty or contains illegal chars [,](ASCII Dec 44) or [`](ASCII Dec 96)");
			selecting = false;
		}
		// unless condition is false, begin loop
		while (selecting){
			// the workBuffer is not initialised here as the method of drawring freehand is the result of the cursor not being a temporary addition to the workBuffer.
			workBuffer.drawCursor(freehandString);
			// print workBuffer
			print(workBuffer.returnBuffer());
			System.out.print("\nDrawing freehand\nInput:");
			// pause for user input.
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// copy workBuffer into buffer and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// ensure that cursor coordinate lies within the bonds of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing eraser</p>
	 * */
	public void placeEraser(){
		// record origin coordinate
		workBuffer.setOrigin();
		// prepare loop condition and start loop
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// draw rectangle with space character between origin and cursor on workBuffer.
			workBuffer.drawRectangle(' ');
			// draw box with space character between origin and cursor on workBuffer.
			workBuffer.drawBox('.',',');
			// draw cursor on workBuffer
			workBuffer.drawCursor("+");
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nEraser\nInput:");
			// loop pauses for user input 
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// initialise workBuffer
				initialiseWork();
				// draw rectangle with space character on workBuffer
				workBuffer.drawRectangle(' ');
				// commit work to buffe and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// ensure that cursor coordinate lies within the bonds of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing cut area</p>
	 * */
	public void placeCut(){
		// set origin coordinate
		workBuffer.setOrigin();
		// prepare loop condition and begin loop
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// draw box on workBuffer between origin and cursor
			workBuffer.drawBox('.',',');
			// draw cursor on workBuffer
			workBuffer.drawCursor("+");
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			// print workBuffer to system.out
			System.out.print("\nCutting area\nInput:");
			// pause for user input
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// intialise workBuffer
				initialiseWork();
				// copy region between origin and cursor to clipboard
				workBuffer.copy();
				// draw rectangle with space character between origin and cursor
				workBuffer.drawRectangle(' ');
				// commit work to buffer and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// ensure that cursor coordinate lies within the bonds of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing copy area</p>
	 * */
	public void placeCopy(){
		// set origin coordinate
		workBuffer.setOrigin();
		// prepare loop condition and begin loop
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// draw box between origin and cursor on workBuffer
			workBuffer.drawBox('.',',');
			// draw cursor on workBuffer.
			workBuffer.drawCursor("+");
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nCopying area\nInput:");
			// wait for user input.
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// initialise workBuffer
				initialiseWork();
				// copy region between origin and cursor to clipboard
				workBuffer.copy();
				// commit work to buffer and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// ensure that cursor coordinate lies within the bonds of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>Control loop for placing paste area</p>
	 * @param mode Determines whether to paste with/without blankspace as 0/1 respectively
	 * */
	public void placePaste(int mode){
		// prepare loop condition and begin loop
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// paste clipboard at the cursor coordinate on workBuffer
			workBuffer.paste(mode);
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nPasting area\nInput:");
			// wait for user input
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// initialise workBuffer
				initialiseWork();
				// paste clipboard at the cursor coordinate on workBuffer
				workBuffer.paste(mode);
				// commit work to buffer and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
		}
	}
	/** <p>Control loop for placing boundary fill</p>
	 * */
	public void placeFill(){
		// prepare loop condition
		selecting = true;
		// declare replace character variable
		char replace;
		// ask user to input the fill character
		System.out.print("\nFill character:");
		// pause for user input
		String input = scanner.nextLine();
		// check input for illegal characters (those that would interfere with the shape of the buffer such as tab or space or those that would break the compression and decompression used for saving files.)
		if (input.contains(",") || input.contains("`") || input.contains("	") || input.equals("")){
			// If illegal then issue warning and set loop condition false
			System.out.println("error: Cannot selected this character, empty or contains illegal chars [,](ASCII Dec 44) or [`](ASCII Dec 96)");
			selecting = false;
		}else{
			// set replace variable to the first character in the input
			replace = input.charAt(0);
			// begin control loop if condition is true
			while (selecting){
				// initialise workBuffer
				initialiseWork();
				// boundary fill workBuffer with replace from cursor coorindate
				workBuffer.boundaryFill(replace);
				// draw cursor on workBuffer
				workBuffer.drawCursor("+");
				// print workBuffer to system.out
				print(workBuffer.returnBuffer());
				System.out.print("\nBoundary character fill\nInput:");
				// pause for user input
				String selection = scanner.nextLine();
				if (selection.equals("")){
					// initialise workBuffer
					initialiseWork();
					// boundary fill workBuffer with replace from cursor coorindate
					workBuffer.boundaryFill(replace);
					// commit work to buffer and set loop condition false.
					commitWork();
					selecting = false;
				}else if (selection.equals("q")){
					// loop condition set false with no further action, discarding changes made in the workBuffer.
					selecting = false;
				}else{
				// Any other input is handled by the positionCursor method.
					workBuffer.positionCursor(selection);
				}
				// ensure that cursor coordinate lies within the bonds of the buffer.
				workBuffer.checkForBoundary(buffer.returnBuffer());
			}
		}
	}
	/** <p>Control loop for placing shaded boundary fill</p>
	 * */
	public void placeShadedFill(){
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// shaded fill workBuffer from cursor coorindate
			workBuffer.boundaryFillShaded('|',':','.');
			// draw cursor on workBuffer
			workBuffer.drawCursor("+");
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			System.out.print("\nBoundary shaded fill\nInput:");
			String selection = scanner.nextLine();
			if (selection.equals("")){
				// initialise workBuffer
				initialiseWork();
				// boundary fill workBuffer with replace from cursor coorindate
				workBuffer.boundaryFillShaded('|',':','.');
				// commit work to buffer and set loop condition false.
				commitWork();
				selecting = false;
			}else if (selection.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the positionCursor method.
				workBuffer.positionCursor(selection);
			}
			// ensure that cursor coordinate lies within the bonds of the buffer.
			workBuffer.checkForBoundary(buffer.returnBuffer());
		}
	}
	/** <p>The controlFileMenu method allows control over a filemenu in the directory specified by the supplied argument</p>
	 * @param directory Pathname of directory that is to be navigated
	 * */
	public void controlFileMenu(String directory){
		// prepare loop condition and begin loop
		selecting = true;
		while (selecting){
			// generate an array of strings containg the filenames of all files in the suuplied directory
			String[] fileList = fileHandler.getFiles(directory);
			// draw the filemenu into the filemenu class buffer
			fileMenu.paintView(directory);
			// print the filemenu buffer to system.out
			print(fileMenu.returnBuffer());
			// ask for input and make/print scrollbar which represents the position in the filemenu.
			System.out.print("\nFile menu " + fileMenu.makeScrollbar(dimensions[1]*characterDimensionsRatioHW - 12,fileList.length,'|','-') + "\nInput:");
			// pause for user input.
			String fileOpt = scanner.nextLine();
			// load file
			if (fileOpt.equals("")){
				// read file selected in filemenu into the main buffer with overwrite mode (0)
				fileHandler.readInCompressed(buffer.returnBuffer(),directory + fileList[fileMenu.returnMenuPosition()],0);
				// record state of buffer into history
				buffer.recordBuffer();
				// set loop condition false
				selecting = false;
			// merge file
			}else if (fileOpt.equals("merge")){
				// read file selected in filemenu into the main buffer with merge mode (1)
				fileHandler.readInCompressed(buffer.returnBuffer(),directory + fileList[fileMenu.returnMenuPosition()],1);
				// record state of buffer into history
				buffer.recordBuffer();
				// set loop condition false
				selecting = false;
			// overwrite file
			}else if (fileOpt.equals("save")){
				// ask user for confirmation
				System.out.print("\nOverwrite " + fileList[fileMenu.returnMenuPosition()] + "? [y/n]:");
				// pasue for input
				String confirmation = scanner.nextLine();
				// check input for confirmation
				if (confirmation.equals("y")){
					// writeout compressed buffer to file selected in menu
					fileHandler.writeOutCompressed(buffer.returnBuffer(),directory + fileList[fileMenu.returnMenuPosition()]);
					// set loop condition false
					selecting = false;
				}else{
					// output dialogue if cancelled.
					System.out.println("Overwrite cancelled.");
				}
			// save new file
			}else if (fileOpt.equals("new")){
				// ask for filename to save as
				System.out.print("\nSave as filename (Empty to cancel) [.csv]:");
				// wait for input
				String fileName = scanner.nextLine() + ".csv";
				// check for abort situation
				if (!fileName.equals(".csv")){
					// writeout compressed buffer to file with specified name
					fileHandler.writeOutCompressed(buffer.returnBuffer(),directory + fileName);
				}else{
					// output dialogue if cancelled.
					System.out.println("Operation cancelled.");
				}
			// exit menu
			}else if (fileOpt.equals("q")){
				// loop condition set false with no further action, discarding changes made in the workBuffer.
				selecting = false;
			}else{
				// Any other input is handled by the navigateMenu method, which controls the positioning of the menu.
				fileMenu.navigateMenu(fileOpt,fileList.length);
			}
		}
	}
	/** <p>Control loop for placing mandelbrot</p>
	 * */
	public void placeMandelbrot(){
		// variables x,y,scale determine view position of mandelbrot
		double x = -2.25;
		double y = -1.5;
		double scale = 3.0;
		// speed is a multiplier for the zoom and view movement operations
		double speed = 0.1;
		// boolean sentMessage is the condition deciding wether to display the controls.
		boolean sentMessage = false;
		// prepare loop condition and begin loop
		selecting = true;
		while (selecting){
			// initialise workBuffer
			initialiseWork();
			// draw mandelbrot on workBuffer
			workBuffer.mandelbrot(x,y,scale);
			// print workBuffer to system.out
			print(workBuffer.returnBuffer());
			// print information about view
			System.out.print("\nxPos:" + x + " yPos:" + y + " scale:" + scale + "\nInput:");
			// if message hasn't been sent
			if (!sentMessage){
				// print controls
				System.out.print("	([ijkl]/r: Move/Reset view, o/ENTER: Zoom out/in, p/q: Commit/Exit view)");
				// message has been sent
				sentMessage = true;
			}
			// pause for user input
			String control = scanner.nextLine();
			// check input
			switch (control){
				case "i":
					// move y position by scaled factor speed * scale
					y = y - speed*scale;
					break;
				case "j":
					// move x position by scaled factor speed * scale
					x = x - speed*scale;
					break;
				case "k":
					// move y position by scaled factor speed * scale
					y = y + speed*scale;
					break;
				case "l":
					// move x position by scaled factor speed * scale
					x = x + speed*scale;
					break;
				case "o":
					// zoom view by scaled factor 
					scale = scale*(1+(speed));
					// shift view by half width to zoom into center of screen
					y = y - (speed/2)*scale;
					x = x - (speed/2)*scale;
					break;
				case "":
					// zoom view by scaled factor 
					scale = scale*(1-(speed/2));
					// shift view by half width to zoom into center of screen
					y = y + (speed/4)*scale;
					x = x + (speed/4)*scale;
					break;
				case "r":
					// reset x,y,scale values to their original values
					x = -2.25;
					y = -1.5;
					scale = 3;
					break;
				case "p":
					// commit workbuffer to buffer
					commitWork();
					// set loop condition false
					selecting = false;
					break;
				case "q":
					// loop condition set false with no further action, discarding changes made in the workBuffer.
					selecting = false;
					break;
			}
		}
	}
	/** <p>Sync buffer into workBuffer</p>
	 * */
	public void initialiseWork(){
		// sync buffer into workBuffer
		workBuffer.syncBuffer(buffer.returnBuffer());
	}
	/** <p>Commit work done in workBuffer to the buffer, record history and set file type to 0</p>
	 * */
	public void commitWork(){
		// sync workBuffer into buffer
		buffer.syncBuffer(workBuffer.returnBuffer());
		// record state of buffer to history
		buffer.recordBuffer();
	}
	/** <p>Print a supplied string for a specified number of repeats</p>
	 * @param number Number of repeats
	 * @param theString String to be repeated
	 * */
	public void printMany(double number, String theString){
		// for the number of times specified
		for (int i = 0; i < number; i++){
			// print theString to system.out
			System.out.print(theString);
		}
	}
	/** <p>Print the contents of a referenced buffer in a correctly formatted way</p>
	 * @param buffer Array to be printed
	 * */
	public void print(char[][] buffer){
		// clear terminal screen
		/*regular method clear*///System.out.print('\u000C');
		/*linux terminal clear*/ System.out.print("\033[H\033[2J");System.out.flush();
		// iterate through the buffer array left to right downwards
		// for every row
		for(int i = 0; i < buffer.length; i++){
			// unless printing the first row print new line at the start of each row
			if (i != 0){
				System.out.print("\n");
			}
			// for every column in row
			for(int j = 0; j < buffer[i].length; j++){
				// print character at row/column
				System.out.print(buffer[i][j]);
				// print number of spaces to stretch screen depending on characterDimensionsRatioHW
				printMany(characterDimensionsRatioHW - 1," ");
			}
		}
		// print newline
		System.out.print("\n");
		// print underscores for the width of the buffer
		printMany(dimensions[1]*characterDimensionsRatioHW,"_");
	}
}
