package br.com.gerqs.lunapptico.fragmentController;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import br.com.gerqs.lunapptico.R;
import br.com.gerqs.lunapptico.activities.AprenderPalavras;
import br.com.gerqs.lunapptico.tools.RoundCorners;

public class FragmentEscolhaCenario extends Fragment {
    // declaração da interface de comunicação
    Context context;
    public interface InterfaceComunicao {

        // aqui, um ou mais métodos de comunicação
        void setCenario(String cenario);
    }

    /* variável que representa quem vai receber a atualização dos dados,
       no caso a activity principal, que vai implementar a interface de comunicação */
    private InterfaceComunicao listener;

    @Override
    public void onAttach(Context con) {
        super.onAttach(con);
        context = con;
        if (con instanceof InterfaceComunicao) {
            listener = (InterfaceComunicao) con;
        } else {
            throw new RuntimeException("Activity deve implementar ExemploFragment.InterfaceComunicao");
        }
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_escolha_cenario, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        ImageView objetos = (ImageView) getView().findViewById(R.id.objetos);
        ImageView animais = (ImageView) getView().findViewById(R.id.animais);
        Button home = (Button) getView().findViewById(R.id.escolhaCenarioHome);

        //arredonda os cantos da imgview
        RoundCorners roundCorners = new RoundCorners(context, getResources());
        animais.setImageDrawable(roundCorners.arredondarPorID(R.drawable.floresta));
        objetos.setImageDrawable(roundCorners.arredondarPorID(R.drawable.fazenda));

        //início dos listenners
        animais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setCenario("animals");
            }
        });

        objetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setCenario("objects");
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AprenderPalavras.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }
}