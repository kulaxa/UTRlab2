import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        int numOfStates, maxRecDepth, numOfSymbols, stateAndSymLength, numOfGoodStates;
        String input= new String(Files.readAllBytes(Path.of("inputFile.txt")));
        String inputs[] = input.split(";");
        numOfStates = Integer.parseInt(inputs[0]);

        maxRecDepth = Integer.parseInt(inputs[1]);
        numOfSymbols = Integer.parseInt(inputs[2]);
        stateAndSymLength = Integer.parseInt(inputs[3]);
        String states[] = new String[numOfStates];
        String symbols[] = new String[numOfSymbols];
        numOfGoodStates = numOfStates; //mogu kasnije staviti da se mogu učitait
        Random rng = new Random(states.hashCode());
        StringBuilder builderInit = new StringBuilder();
        for(int i=0; i<numOfStates; i++){
            for(int j=0; j<stateAndSymLength;j++){
                builderInit.append(Math.abs(rng.nextInt()%10));

            }
            states[i] = builderInit.toString();
            builderInit.setLength(0);
        }
        builderInit.setLength(0);

        for(int i=0; i<numOfSymbols; i++){
            for(int j=0; j<stateAndSymLength;j++){
                builderInit.append((char) (Math.abs(rng.nextInt()%('z'-'a'))+'a'));

            }
            symbols[i] = builderInit.toString();
            builderInit.setLength(0);
        }


        try (FileWriter writer = new FileWriter("test.ul")){
            StringBuilder builder = new StringBuilder();
            for(int i=0; i<numOfStates-1; i++){
                    builder.append(states[i] + ",");
            }
            builder.append(states[numOfStates-1]);
            builder.append("\n");

            for(int i=0; i<numOfSymbols-1; i++){
                builder.append(symbols[i] + ",");
            }
            builder.append(symbols[numOfSymbols-1]);
            builder.append("\n");


            List<String> randomList = new ArrayList<>();
            randomList.addAll(List.of(states));
            Collections.shuffle(randomList);
            for(int i=0; i<numOfGoodStates-1; i++){
                builder.append(randomList.get(i)+",");
            }
            builder.append(randomList.get(numOfGoodStates-1)+"\n");
            builder.append(states[0]+"\n");
            for(int i=0; i<numOfStates; i++){

                    for (int j = 0; j < numOfSymbols; j++) {
                        builder.append(states[i] + "," + symbols[j] + "->" + states[(i + 1 + j) % numOfStates] + "\n");
                    }


            }
            writer.append(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileWriter writer = new FileWriter("test.izl")){
            StringBuilder builder = new StringBuilder();
            builder.append(states[0] +"\n");
            for(int i=0; i<numOfSymbols-1; i++){
                builder.append(symbols[i]+",");
            }
            builder.append(symbols[numOfSymbols-1]+"\n");
            builder.append(states[0] +"\n");
            builder.append(states[0] +"\n");
            for(int i=0; i<numOfSymbols; i++){
                builder.append(states[0]+","+symbols[i]+"->"+states[0]+"\n");
            }
            writer.append(builder.toString());
        }
    }
}
