
package castle.original;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 77781225
 */
public class AnonymizationOutput {
    Tuple tuple;
    Cluster cluster;
    double InfoLoss;

    public AnonymizationOutput(Tuple tuple, Cluster cluster) {
        this.tuple = tuple;
        this.cluster = cluster;
        cluster.tuples=null;
    }
}
