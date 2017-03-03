package sri;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Onichan
 */
class Pair<F, S> {

    private F first; //first member of pair
    private S second; //second member of pair

    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    void setFirst(F first) {
        this.first = first;
    }

    void setSecond(S second) {
        this.second = second;
    }

    F getFirst() {
        return first;
    }

    S getSecond() {
        return second;
    }
}