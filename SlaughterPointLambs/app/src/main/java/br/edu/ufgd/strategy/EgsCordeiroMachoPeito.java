package br.edu.ufgd.strategy;

import android.util.Log;

import br.edu.ufgd.utils.Constantes;

public class EgsCordeiroMachoPeito extends Egs {

    public EgsCordeiroMachoPeito(double peso, double prega, String spCategoria) {
        this.peso = peso;
        this.prega = prega;
        this.spCategoria = spCategoria;
    }

    public double CalcularEgs() {

        double egs = -3.99 + (0.588 * peso) - (0.716 * prega) - 0.00958 * Math.pow(peso, 2) + 0.0473 * Math.pow(prega, 2);

        setEgs(egs);

        strFormulaUtilizada = Constantes.FORMULA_CORDEIRO_MACHO_PEITO;

        Log.i(TAG, getClassName() + ".CalcularEgs(): " + egs);

        return egs;
    }
}
