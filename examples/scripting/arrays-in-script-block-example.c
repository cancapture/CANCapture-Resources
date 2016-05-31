void OnStartCapture(ScriptBlock block)
{
       int i;
       int[] testarray(10);  //Create an array of 10 elements
       for (i = 0; i < 10; ++i)
              testarray[i] = i * 5;
 
       string str = "Testing array\n";
       for (i = 0; i < 10; ++i)
              str = str + testarray[i] + "\n";
 
       block.ClearOutputText();
       block.PrintOutputText("Capture started\n", false, false, true, true);
       block.PrintOutputText(str, false, false, false, true);
}
 
void OnStopCapture(ScriptBlock block)
{
       block.ClearOutputText(); //Clear this scripts output window
       block.PrintOutputText("Capture stopped\n", false, false, false, false); 
//Print to the output window
}