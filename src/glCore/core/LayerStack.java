package glCore.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LayerStack {
    private List<Layer> _layers;
    private int _layerInsertIndex;

    public LayerStack(){
        _layers = new ArrayList<>();
        _layerInsertIndex = 0;
    }

    public void pushLayer(Layer layer){
        _layers.add(_layerInsertIndex, layer);
        _layerInsertIndex++;
        layer.onAttach();
    }

    public void pushOverlay(Layer overlay){
        _layers.add(overlay);
        overlay.onAttach();
    }

    public void popLayer(Layer layer){
        if(_layers.contains(layer) && _layers.indexOf(layer) < _layerInsertIndex){
            layer.onDetach();
            _layers.remove(layer);
            _layerInsertIndex--;
        }
    }

    public void popOverlay(Layer overlay){
        if(_layers.contains(overlay) && _layers.indexOf(overlay) >= _layerInsertIndex){
            overlay.onDetach();
            _layers.remove(overlay);
        }
    }

    public final List<Layer> getLayers(){
        return _layers;
    }

    public Iterator<Layer> iterator(){
        return _layers.iterator();
    }

    public void disposeLayers(){
        for(Layer lay : _layers){
            lay.onDetach();
        }

        _layers.clear();
    }
}
