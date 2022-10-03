package glCore.renderer;

import java.util.ArrayList;
import java.util.List;

public class VertexBufferLayout {
    private List<VertexBufferElement> _elements;
    private int _stride;

    public VertexBufferLayout(){
        _elements = new ArrayList<>();
        _stride = 0;
    }

    public void pushElements(int attribIndex, int count, boolean normalize){
        _elements.add(new VertexBufferElement(attribIndex, count, normalize, _stride));
        _stride += count * Float.BYTES;
    }

    public int getStride(){
        return _stride;
    }

    public final List<VertexBufferElement> getElements(){
        return _elements;
    }
}
