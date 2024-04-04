package pt.tecnico.ulisboa.hbbft.subset;

public interface SubsetFactory {
    IAsynchronousCommonSubset create();
    IAsynchronousCommonSubset create(String pid);
}
