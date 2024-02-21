public class StringHandler {
    private String doc;
    private int index;
    public StringHandler(String lexerString){
        this.doc=lexerString;
        this.index=0;
    }

    /**
     * @param peekIndex The index you are peeking.
     * @return character at that index
     */
    public char Peek(int peekIndex) {
        if (index + peekIndex < doc.length()) {
            return doc.charAt(index + peekIndex);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds, You cant peek something that doesn't Exists ");
        }    }

    /**
     *
     * @param  peekIndex index you are peeking
     * @return substring starting from index to peek.
     */
    public String PeektoString(int peekIndex) {

        if (index + peekIndex <= doc.length()) {
            return doc.substring(index, index + peekIndex);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds, You cant peek something that doesn't Exists ");
        }

    }

    /**
     * @return character at that index
     */
    public char getChar(){

        return doc.charAt(index++);


    }

    /**
     * Moves the index ahead.
     * @param swallowIndex
     */
    public void Swallow(int swallowIndex) {
        index = index + swallowIndex;
    }


    /**
     * @return true or false if doc is done
     */
    public boolean isDone() {
        return index >= doc.length();

    }

    /**
     * @return doc starting index
     */
    public String reminader() {
        return doc.substring(index);
    }
}


