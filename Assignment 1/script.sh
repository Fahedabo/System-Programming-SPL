#!/bin/sh

bin/cRace customTests/shouldTerminate1.json
bin/cRace customTests/shouldTerminate2.json
bin/cRace customTests/lastJoinPolicy1.json
bin/cRace customTests/mandatesJoinPolicy2.json
bin/cRace customTests/mandatesJoinPolicy3.json
bin/cRace customTests/EdgeSelectionPolicy1.json
bin/cRace customTests/EdgeSelectionPolicy2.json
bin/cRace customTests/MandatesSelectionPolicy1.json
bin/cRace customTests/MandatesSelectionPolicy2.json

diff -s customTests/shouldTerminate1_sol.out customTests/shouldTerminate1.out
diff -s customTests/shouldTerminate2_sol.out customTests/shouldTerminate2.out
diff -s customTests/lastJoinPolicy1_sol.out customTests/lastJoinPolicy1.out
diff -s customTests/mandatesJoinPolicy2_sol.out customTests/mandatesJoinPolicy2.out
diff -s customTests/mandatesJoinPolicy3_sol.out customTests/mandatesJoinPolicy3.out
diff -s customTests/EdgeSelectionPolicy1_sol.out customTests/EdgeSelectionPolicy1.out
diff -s customTests/EdgeSelectionPolicy2_sol.out customTests/EdgeSelectionPolicy2.out
diff -s customTests/MandatesSelectionPolicy1_sol.out customTests/MandatesSelectionPolicy1.out
diff -s customTests/MandatesSelectionPolicy2_sol.out customTests/MandatesSelectionPolicy2.out

valgrind --leak-check=full bin/cRace customTests/shouldTerminate1.json
valgrind --leak-check=full bin/cRace customTests/shouldTerminate2.json
valgrind --leak-check=full bin/cRace customTests/lastJoinPolicy1.json
valgrind --leak-check=full bin/cRace customTests/mandatesJoinPolicy2.json
valgrind --leak-check=full bin/cRace customTests/mandatesJoinPolicy3.json
valgrind --leak-check=full bin/cRace customTests/EdgeSelectionPolicy1.json
valgrind --leak-check=full bin/cRace customTests/EdgeSelectionPolicy2.json
valgrind --leak-check=full bin/cRace customTests/MandatesSelectionPolicy1.json
valgrind --leak-check=full bin/cRace customTests/MandatesSelectionPolicy2.json