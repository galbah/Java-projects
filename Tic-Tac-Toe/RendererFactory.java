public class RendererFactory {
    /** this class controls building renderer of board,
     * it returns new variable of type Renderer according to given name and size
     */

    public Renderer buildRenderer(String rendererName, int size) {

        switch (rendererName) {

            case ("none"):
                return new VoidRenderer();
            case ("console"):
                return new ConsoleRenderer(size);
            default:
                return null;
        }
    }
}
