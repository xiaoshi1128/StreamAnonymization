/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fads.varied;


/**
 *
 * @author 77781225
 */
public class AnonymizationOutput {
    Tuple tuple;
    Cluster cluster;
    double InfoLoss;

    public AnonymizationOutput(Tuple tuple, Cluster cluster, double InfoLoss) {
        this.tuple = tuple;
        this.cluster = cluster;
        this.InfoLoss = InfoLoss;
    }
}
