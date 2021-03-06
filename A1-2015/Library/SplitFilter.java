import java.util.*;
import java.io.*;

/**
 * Provides a generic way to split a filter that produces Frames into n outputs.
 *
 * Users are expected to use the ConnectOutput method to add an output filter to the network.
 *
 */

public class SplitFilter extends FrameFilterFramework {
    private ArrayList<PipedOutputStream> outputs = new ArrayList<>();

    /**
     * Use this method to add an output to the split filter.
     * @param output
     */

    void ConnectOutput(FilterFramework output) {
        try {
            PipedOutputStream p = new PipedOutputStream();
            output.InputReadPort.connect(p);
            System.out.println("Connected outplut " + output.getName() + " to split filter: " + this.getName());
            output.InputFilter = this;
            outputs.add(p);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.print( "\n" + this.getName() + "::SplitFilter ");
        while (true) {
            try {
                Frame frame = readFrame(this.InputReadPort);
                for (PipedOutputStream output : outputs) {
                    writeFrame(frame, output);
                }
            }
            catch (EndOfStreamException e) {
                e.printStackTrace();
                for (PipedOutputStream output : outputs) {
                    try {
                        output.close();
                    }
                    catch (Exception e2) {
                        e.printStackTrace();
                    }
                }
                ClosePorts();
                break;
            }
        }
    }
}
