public class Main {
    public static void main(String[]args){
        final int x = 1;
        System.out.println(removeAll("abcdefghi", "def"));

    }
    public static String removeAll(String s, String key) { // "abcdefghi" "def"
        String result="";

        for(int i=0; i<s.length(); ) {
            if(i + key.length() < s.length() && s.substring(i, i+key.length()).equals(key)) {
                i = i+key.length();
            }
            else {
                result += s.charAt(i);
                i++;
            }
        }
        return result;
    }
}
