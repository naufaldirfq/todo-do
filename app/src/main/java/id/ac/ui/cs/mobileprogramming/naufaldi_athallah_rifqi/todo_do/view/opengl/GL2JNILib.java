package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.view.opengl;

// Wrapper for native library

public class GL2JNILib {

    static {
        System.loadLibrary("gl2jni");
    }

    /**
     * @param width the current view width
     * @param height the current view height
     */
    public static native void init(int width, int height);
    public static native void step();
}