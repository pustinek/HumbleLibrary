package me.pustinek.humblelibrary.tests;

public class ItemBuilderTest {

    /*
    @DisplayName("Single test successful")
    @Test
    void testItemBuilderReplacement() {
        final Pattern conditionPattern = Pattern.compile("\\{(con:)(.*)}");

        List<String> addLore = Arrays.asList("This should be displayed", "{con:mode_false} This should NOT be displayer");
        HashMap<String, String> loreConditions = new HashMap<>();

        loreConditions.put("mode_true", "true");
        loreConditions.put("mode_false", "false");

        ListIterator<String> iterator = addLore.listIterator();
        List<String> actualList = new ArrayList<String>();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Matcher matcher = conditionPattern.matcher(next);
            int matches = 0;
            boolean matchFound = matcher.find();

            System.out.println("matches (" + matchFound + ") (" + matchFound + ")  -> " + next + "\n");
            if (!matchFound) {
                actualList.add(next);
                continue;
            }

            int index = 0;
            String group = matcher.group(1);
            if (group == null) {
                actualList.add(next);
                continue;
            }

            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");

            loreConditions.forEach((con, res) -> {
                try {
                    engine.eval(con + " = " + res);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            });


            try {
                Boolean passed = (Boolean) engine.eval(group);
                if (passed) {
                    actualList.add(next);
                    continue;
                }

                String finalString = next.replace(matcher.group(0), "");
                iterator.set(finalString);
            } catch (ScriptException e) {
                e.printStackTrace();
                iterator.remove();
            }
        }


        System.out.print("finished");
        System.out.print(actualList.toString());
    }

    @Test
    public void testConditionalLore() {

        final Pattern conditionPattern = Pattern.compile("\\{(con:)(.*)}");

        List<String> addLore = Arrays.asList("{con:mode_true}This should be displayed", "{con:mode_false} This should NOT be displayer");
        HashMap<String, String> loreConditions = new HashMap<>();

        loreConditions.put("mode_true", "true");
        loreConditions.put("mode_false", "false");

        ListIterator<String> iterator = addLore.listIterator();
        List<String> actualList = new ArrayList<String>();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Matcher matcher = conditionPattern.matcher(next);
            int matches = 0;
            boolean matchFound = matcher.find();

            System.out.println("matches (" + matchFound + ") -> " + next + "\n");
            if (!matchFound) {
                actualList.add(next);
                continue;
            }

            int index = 0;
            String group = matcher.group(2);
            if (group == null) {
                actualList.add(next);
                continue;
            }

            System.out.println("group_experssion: " + group);
            System.out.println("total_found: " + matcher.group(0));
            Expression exp = new Expression(group);

            loreConditions.forEach(exp::setVariable);

            BigDecimal eval = exp.eval();


            if (eval.intValue() > 0) actualList.add(next.replace(matcher.group(0), ""));
        }


        System.out.print("finished");
        System.out.print(actualList.toString());

    }

     */
}
