class Constants {
    static DB_CONNECTION_STRING: string = "mongodb://Unsws-MacBook-Pro.local:27017/indexapi";
    static jwtSecret: string = "indexapit3!lx+2o=qnevr**_hjb5csp*ye4@&jra_4juekim0m&ecj)+j";
    static jwtSession = { session: false };
}
Object.seal(Constants);
export default Constants;