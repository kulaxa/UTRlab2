import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class State {
    public static List<State> allStates = new ArrayList<>();

    private String name;
    private Map<String, State> transitions;
    private boolean isGood;
    private List<State> equivalentStates;

    public State(String name) {
        this.name = name;
        transitions = new HashMap<>();
        isGood = false;
        equivalentStates = new ArrayList<>();
        allStates.add(this);
    }

    public String getName() {
        return this.name;
    }
    public void setIsGood(boolean good){
        this.isGood = good;
    }
    public void addEquivalentStates(State state){
        equivalentStates.add(state);
    }
    public List<State> getEquivalentStates(){
        return equivalentStates;
    }

    public boolean isGood(){
        return isGood;
    }

    public void addNewTransition(String symbol, State state) {
        transitions.put(symbol, state);
    }

    public static State getStateByName(String name) {
        return allStates.stream().filter((s) -> s.name.equals(name)).findAny().get();
    }

    public static void testPrintTransitions() {
        allStates.stream().forEach((s) -> s.transitions.entrySet().stream().forEach(
                (entry) -> {
                        
                     System.out.println(entry.getKey() + ", " + entry.getValue().getName());
                }
        ));
    }

    public static List<State> getAllStates() {
        return allStates;
    }

    public static List<State> findAllInescapableStates(State state) {
        List<State> visitedStates = new ArrayList<>();
        findAllInescapableStatesRec(state, visitedStates);

        List<State> result = new ArrayList<>();
        allStates.forEach((st) -> {
            if (!visitedStates.contains(st)) {
                result.add(st);
            }
        });
        return result;

    }

    private static void findAllInescapableStatesRec(State state, List<State> visitedStates) {
        visitedStates.add(state);
        state.transitions.values().forEach((st) -> {
            if (!visitedStates.contains(st)) { //ako to stanje već nije posjećeno
                findAllInescapableStatesRec(st, visitedStates);
            }
        });
    }

    public Map<String, State> getTransitions(){
        return transitions;
    }

    public static void mainAlgorithm(State firstState) {
        //main algorithm

    }

    public static boolean checkIfEquivalent(State firstState, State secondState, String[] symbols, List<State> visited) {

        if (firstState.isGood != secondState.isGood || (visited.contains(firstState) || visited.contains(secondState))) return false;
        visited.add(firstState);
        visited.add(secondState);
            for (String sym : symbols) {
                return checkIfEquivalent(firstState.transitions.get(sym), secondState.transitions.get(sym), symbols,visited);
            }
            return true;


    }
}

public class lab2 {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line= reader.readLine();
        String allStates[] = line.split(",");
        for(String s: allStates){
            new State(s);
        }

        line = reader.readLine();
        String allSymbols[] = line.split(",");
        line = reader.readLine();
        String goodStates[] = line.split(",");
        for(String s: goodStates){
            State.getStateByName(s).setIsGood(true);
        }
        String startingStateString = reader.readLine();
        State startingState = State.getStateByName(startingStateString);

        List<String[]> transitions = new ArrayList<>();
        while(true){
            line = reader.readLine();
            if(line== null){break;}
            String transition[]= line.split("->");
            String fromState[] = transition[0].split(",");
            State.getStateByName(fromState[0]).addNewTransition(fromState[1],
                    State.getStateByName(transition[1]));
        }
        //find all inescapable states
        List<State> inescapableState = State.findAllInescapableStates(startingState);

        inescapableState.forEach((state)->{
           //System.out.println("removing: "+state.getName());
            State.getAllStates().remove(state);});
        for (State firststate : State.getAllStates()) { //mogla bi biti dvostruko brza petlja da je tablica(yikes)
            for (State secondstate : State.getAllStates()) {
                if (firststate != secondstate) {
                    List<State> visited = new ArrayList<>();
                    if(State.checkIfEquivalent(firststate,secondstate, allSymbols,visited)){
                        firststate.addEquivalentStates(secondstate);
                       System.out.println(firststate.getName()+ " is eq with "+secondstate.getName());
                    }
                    else{
                       // System.out.println(firststate.getName()+ " isn't eq with "+secondstate.getName());

                    }

                }
            }
        }

        StringBuilder builder = new StringBuilder();
        List<State> differentStates = new ArrayList<>();
        for(State state:State.getAllStates()){
            boolean differentState= true;
            for(State st: state.getEquivalentStates()){
                if(differentStates.contains(st)){
                    differentState = false;
                    break;
                }

            }
            if(differentState) {differentStates.add(state);};
        }
        for(int i=0; i<differentStates.size(); i++){
            builder.append(differentStates.get(i).getName());
            if(i!=differentStates.size()-1){
                builder.append(",");
            }
        }
        builder.append("\n");
        for(int i=0; i<allSymbols.length; i++){
            builder.append(allSymbols[i]);
            if(i!=allSymbols.length-1){
                builder.append(",");
            }
        }
        builder.append("\n");

        for(int i=0;i<differentStates.size(); i++){
            if(differentStates.get(i).isGood()){
                builder.append(differentStates.get(i).getName());
                if(i!=differentStates.size()-1){
                    builder.append(",");
                }
            }

        }
        builder.append("\n");
        builder.append(startingStateString+"\n");

        for(State state: differentStates){
            state.getTransitions().entrySet().forEach((entry)->{
                builder.append(state.getName()+","+entry.getKey()+"->"+entry.getValue().getName()+"\n");
            });
        }

        System.out.print(builder.toString());

        }




    }

