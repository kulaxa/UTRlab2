import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class State {
    public static List<State> allStates = new ArrayList<>();
    private String name;
    private Map<String, State> transitions;
    private boolean isGood;
    private List<State> equivalentStates;

    public State(String name) {
        this.name = name;
        transitions = new LinkedHashMap<>();
        isGood = false;
        equivalentStates = new ArrayList<>();
        allStates.add(this);
    }

    public String getName() {
        return this.name;
    }

    public void setIsGood(boolean good) {
        this.isGood = good;
    }

    public void addEquivalentStates(State state) {
        equivalentStates.add(state);
    }

    public List<State> getEquivalentStates() {
        return equivalentStates;
    }

    public boolean isGood() {
        return isGood;
    }

    public void addNewTransition(String symbol, State state) {
        transitions.put(symbol, state);
    }

    public static State getStateByName(String name) {
        return allStates.stream().filter((s) -> s.name.equals(name)).findAny().get();
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
            if (!visitedStates.contains(st)) { //ako to stanje vec nije posjeceno
                findAllInescapableStatesRec(st, visitedStates);
            }
        });
    }

    public Map<String, State> getTransitions() {
        return transitions;
    }

    public static boolean checkIfEquivalent(State firstState, State secondState, String[] symbols, Map<State, List<State>> visited) {
        if (firstState.isGood != secondState.isGood) return false;
        if (firstState== secondState) return true;
        if(visited.get(firstState) != null)
            if(visited.get(firstState).contains(secondState))
                return true;
        if(visited.get(secondState) != null)
         if(visited.get(secondState).contains(secondState))
             return true;
        if(visited.containsKey(firstState)){
            visited.get(firstState).add(secondState);
        }
        else{
            visited.put(firstState, new ArrayList<>());
        }
        for (String sym : symbols) {
             if(!checkIfEquivalent(firstState.transitions.get(sym), secondState.transitions.get(sym), symbols, visited)){
                return false;
            }
        }
        return true;
    }
}

public class MinDka {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        String allStates[] = line.split(",");
        for (String s : allStates) {
            new State(s);
        }
        line = reader.readLine();
        String allSymbols[] = line.split(",");
        line = reader.readLine();
        String goodStates[] = line.split(",");
        for (String s : goodStates) {
            if (s.equals("")) {
                // System.out.println("no good states");
            } else {
                State.getStateByName(s).setIsGood(true);
            }
        }
        String startingStateString = reader.readLine();
        State startingState = State.getStateByName(startingStateString);

        List<String[]> transitions = new ArrayList<>();
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            String transition[] = line.split("->");
            String fromState[] = transition[0].split(",");
            State.getStateByName(fromState[0]).addNewTransition(fromState[1],
                    State.getStateByName(transition[1]));
        }
        //find all inescapable states
        List<State> inescapableState = State.findAllInescapableStates(startingState);

        inescapableState.forEach((state) -> {
            State.getAllStates().remove(state);
        });
        for (State firststate : State.getAllStates()) {
            for (State secondstate : State.getAllStates()) {
                if (firststate != secondstate) {
                    Map<State, List<State>> visited = new LinkedHashMap<>();
                    if (State.checkIfEquivalent(firststate, secondstate, allSymbols, visited)) {
                        firststate.addEquivalentStates(secondstate);
                       System.out.println(firststate.getName()+ " is eq with "+secondstate.getName());
                    }

                }
            }
        }

        StringBuilder builder = new StringBuilder();
        List<State> differentStates = new ArrayList<>();
        for (State state : State.getAllStates()) {
            boolean differentState = true;
            for (State st : state.getEquivalentStates()) {
                if (differentStates.contains(st)) {
                    differentState = false;
                    break;
                }
            }
            if (differentState) {
                differentStates.add(state);
            }
        }
        for (int i = 0; i < differentStates.size(); i++) {
            builder.append(differentStates.get(i).getName());
            if (i != differentStates.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");
        for (int i = 0; i < allSymbols.length; i++) {
            builder.append(allSymbols[i]);
            if (i != allSymbols.length - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");
        List<State> goodStatesList = differentStates.stream().filter((state) -> state.isGood()).collect(Collectors.toList());
        for (int i = 0; i < goodStatesList.size(); i++) {
            builder.append(goodStatesList.get(i).getName());
            if (i != goodStatesList.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("\n");
        if (!differentStates.contains(startingState)) {
            startingStateString = startingState.getEquivalentStates().get(0).getName();
        }
        builder.append(startingStateString + "\n");

        for (State state : differentStates) {
            state.getTransitions().entrySet().forEach((entry) -> {
                boolean written = false;
                for (State s : entry.getValue().getEquivalentStates()) {
                    if (differentStates.contains(s)) {
                        builder.append(state.getName() + "," + entry.getKey() + "->" + s.getName() + "\n");
                        written = true;
                        break;

                    }
                }
                if (!written) {
                    builder.append(state.getName() + "," + entry.getKey() + "->" + entry.getValue().getName() + "\n");
                }
            });
        }
        System.out.print(builder.toString());
    }
}

