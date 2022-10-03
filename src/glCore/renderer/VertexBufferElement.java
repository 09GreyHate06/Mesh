package glCore.renderer;

public class VertexBufferElement {
    public final int attribIndex;
    public final int count;
    public final boolean normalized;
    public final int offset;

    public VertexBufferElement(int attribIndex, int count, boolean normalized, int offset){
        this.attribIndex = attribIndex;
        this.count = count;
        this.normalized = normalized;
        this.offset = offset;
    }
}

