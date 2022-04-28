var os = require("os");
var crypto = require("crypto");
var path = require("path");
var fs = require("fs");
const getDirectorySha = require("./get-dir-sha.js");
const what3Words = require("./what-3-words-hash.js");
const buildPng = require("./build-png");

var HASH_SECRET = "autoauto family";
var BUILD_HASH_IGNORED = ["gen", "genealogy", ".cache", "buildimgs"];

var directory = __dirname.split(path.sep);
var rootDirectory = directory.slice(0, directory.indexOf("TeamCode") + 1).join(path.sep);
var srcDirectory = directory.slice(0, directory.indexOf("src") + 1).join(path.sep);

if (rootDirectory == "" || rootDirectory == path.sep ||
    srcDirectory == "" || srcDirectory == path.sep) throw "Unexpected directory structure";


var mac = "";

try {
    mac = Object.values(os.networkInterfaces()).flat().map(x => x.mac).filter(x => x != '00:00:00:00:00:00')[0];
} catch (e) { }

var computerUniqueIdentifier = ([mac, os.cpus()[0].model, os.hostname(), os.platform()]).join(",");

var computerHash = crypto.createHmac("sha256", HASH_SECRET)
    .update(computerUniqueIdentifier)
    .digest("hex");

var familyTreeRecordsDirectory = path.join(srcDirectory, "main/assets/genealogy");
var familyLineFile = path.join(familyTreeRecordsDirectory, computerHash + ".json");

if (!fs.existsSync(familyTreeRecordsDirectory)) fs.mkdirSync(familyTreeRecordsDirectory, { recursive: true });
if (!fs.existsSync(familyLineFile)) fs.writeFileSync(familyLineFile, "{}"); //SAFE

var familyLine = readJsonFile(familyLineFile);

(async function () {
    if (!familyLine.browser) {
        familyLine.browser = "Removed_for_privacy_reasons_" + Math.round(Math.random() * 0xFF_FF_FF).toString(16);
    }

    if (!familyLine.cognomen) familyLine.cognomen = generateCognomen(computerHash);
    if (!familyLine.builds) familyLine.builds = [];
    if (!familyLine.buildCount) familyLine.buildCount = 0;

    familyLine.buildCount++;

    var name = getName(familyLine.buildCount, familyLine.cognomen);
    var time = (new Date()).toISOString();
    var buildHash = getDirectorySha(srcDirectory, BUILD_HASH_IGNORED);
    var w3w = what3Words.simpleNouns(buildHash);
    var phrase = what3Words.complexPhrase(buildHash);
    var pngFile = buildPng(familyLine.buildCount, srcDirectory, BUILD_HASH_IGNORED);
    
    console.error("AGPBI: " + JSON.stringify({
        kind: "INFO",
        text: "Build Name: " + name
    }));

    familyLine.builds.push({
        name: name,
        time: time,
        buildHash: buildHash,
        w3w: w3w,
        phrase: phrase,
        perceptualHash: pngFile.perceptualDiff,
        colors: pngFile.colors
    });

    familyLine.builds = familyLine.builds.slice(-100);

    familyLine.lastBuildTimeUnixMs = Date.now();

    fs.writeFileSync(familyLineFile, JSON.stringify(familyLine, null, 4)); //SAFE

    var history = fs.readdirSync(familyTreeRecordsDirectory) //get files in the geneology
        .filter(x => x.endsWith(".json")) //only load JSON files
        .map(x => require(path.join(familyTreeRecordsDirectory, x))) //load the data
        .map(x => x.builds) //narrow it down to just the build data
        .flat() //transform the array of family-build-data arrays into just 1 array
        .sort((a, b) => a.time > b.time ? -1 : 1) //sort descending by time; alphabetical will work well for ISO timestamps
        .slice(0, 100) //only get last 100 builds
        .map(x => x.name + "," + x.time + "," + x.w3w) //transform to CSV
        .join("\n") //join CSV rows together

    updateTemplate(familyLine, time, name, history, buildHash, w3w, pngFile.imageAddress, familyLine.buildCount, phrase);
})();

function updateTemplate(familyLine, time, name, history, hash, phrase, pngFileAddress, buildNumber, phraseLong) {
    var template = fs.readFileSync(path.join(__dirname, "not_BuildHistory.notjava")).toString();

    var resPath = path.join(srcDirectory, "../gen/org/firstinspires/ftc/teamcode/auxilary/buildhistory", "BuildHistory.java");
    fs.mkdirSync(path.dirname(resPath), { recursive: true });

    fs.writeFileSync(resPath, template //SAFE
        .replace("BUILDER_BROWSER_FINGERPRINT", familyLine.browser)
        .replace("BUILD_TIME_ISO", time)
        .replace("BUILD_NAME", name)
        .replace("not_BuildHistory", "BuildHistory")
        .replace("BUILD_HISTORY", JSON.stringify(history).slice(1, -1))
        .replace("BUILD_HASH", hash)
        .replace("BUILD_PHRASE", phrase)
        .replace("BUILD_PHRASE_LONG", phraseLong)
        .replace("BUILD_HASH_IMAGE", pngFileAddress)
        .replace("BUILD_COUNT", buildNumber)
    )
}

function getName(index, lastName) {
    var firstNames = ["Liam", "Olivia", "Noah", "Emma", "Oliver", "Ava", "Elijah", "Charlotte", "William", "Sophia", "James", "Amelia", "Benjamin", "Isabella", "Lucas", "Mia", "Henry", "Evelyn", "Alexander", "Harper", "Mason", "Camila", "Michael", "Gianna", "Ethan", "Abigail", "Daniel", "Luna", "Jacob", "Ella", "Logan", "Elizabeth", "Jackson", "Sofia", "Levi", "Emily", "Sebastian", "Avery", "Mateo", "Mila", "Jack", "Scarlett", "Owen", "Eleanor", "Theodore", "Madison", "Aiden", "Layla", "Samuel", "Penelope", "Joseph", "Aria", "John", "Chloe", "David", "Grace", "Wyatt", "Ellie", "Matthew", "Nora", "Luke", "Hazel", "Asher", "Zoey", "Carter", "Riley", "Julian", "Victoria", "Grayson", "Lily", "Leo", "Aurora", "Jayden", "Violet", "Gabriel", "Nova", "Isaac", "Hannah", "Lincoln", "Emilia", "Anthony", "Zoe", "Hudson", "Stella", "Dylan", "Everly", "Ezra", "Isla", "Thomas", "Leah", "Charles", "Lillian", "Christopher", "Addison", "Jaxon", "Willow", "Maverick", "Lucy", "Josiah", "Paisley", "Isaiah", "Natalie", "Andrew", "Naomi", "Elias", "Eliana", "Joshua", "Brooklyn", "Nathan", "Elena", "Caleb", "Aubrey", "Ryan", "Claire", "Adrian", "Ivy", "Miles", "Kinsley", "Eli", "Audrey", "Nolan", "Maya", "Christian", "Genesis", "Aaron", "Skylar", "Cameron", "Bella", "Ezekiel", "Aaliyah", "Colton", "Madelyn", "Luca", "Savannah", "Landon", "Anna", "Hunter", "Delilah", "Jonathan", "Serenity", "Santiago", "Caroline", "Axel", "Kennedy", "Easton", "Valentina", "Cooper", "Ruby", "Jeremiah", "Sophie", "Angel", "Alice", "Roman", "Gabriella", "Connor", "Sadie", "Jameson", "Ariana", "Robert", "Allison", "Greyson", "Hailey", "Jordan", "Autumn", "Ian", "Nevaeh", "Carson", "Natalia", "Jaxson", "Quinn", "Leonardo", "Josephine", "Nicholas", "Sarah", "Dominic", "Cora", "Austin", "Emery", "Everett", "Samantha", "Brooks", "Piper", "Xavier", "Leilani", "Kai", "Eva", "Jose", "Everleigh", "Parker", "Madeline", "Adam", "Lydia", "Jace", "Jade", "Wesley", "Peyton", "Kayden", "Brielle", "Silas", "Adeline", "Bennett", "Vivian", "Declan", "Rylee", "Waylon", "Clara", "Weston", "Raelynn", "Evan", "Melanie", "Emmett", "Melody", "Micah", "Julia", "Ryder", "Athena", "Beau", "Maria", "Damian", "Liliana", "Brayden", "Hadley", "Gael", "Arya", "Rowan", "Rose", "Harrison", "Reagan", "Bryson", "Eliza", "Sawyer", "Adalynn", "Amir", "Kaylee", "Kingston", "Lyla", "Jason", "Mackenzie", "Giovanni", "Alaia", "Vincent", "Isabelle", "Ayden", "Charlie", "Chase", "Arianna", "Myles", "Mary", "Diego", "Remi", "Nathaniel", "Margaret", "Legend", "Iris", "Jonah", "Parker", "River", "Ximena", "Tyler", "Eden", "Cole", "Ayla", "Braxton", "Kylie", "George", "Elliana", "Milo", "Josie", "Zachary", "Katherine", "Ashton", "Faith", "Luis", "Alexandra", "Jasper", "Eloise", "Kaiden", "Adalyn", "Adriel", "Amaya", "Gavin", "Jasmine", "Bentley", "Amara", "Calvin", "Daisy", "Zion", "Reese", "Juan", "Valerie", "Maxwell", "Brianna", "Max", "Cecilia", "Ryker", "Andrea", "Carlos", "Summer", "Emmanuel", "Valeria", "Jayce", "Norah", "Lorenzo", "Ariella", "Ivan", "Esther", "Jude", "Ashley", "August", "Emerson", "Kevin", "Aubree", "Malachi", "Isabel", "Elliott", "Anastasia", "Rhett", "Ryleigh", "Archer", "Khloe", "Karter", "Taylor", "Arthur", "Londyn", "Luka", "Lucia", "Elliot", "Emersyn", "Thiago", "Callie", "Brandon", "Sienna", "Camden", "Blakely", "Justin", "Kehlani", "Jesus", "Genevieve", "Maddox", "Alina", "King", "Bailey", "Theo", "Juniper", "Enzo", "Maeve", "Matteo", "Molly", "Emiliano", "Harmony", "Dean", "Georgia", "Hayden", "Magnolia", "Finn", "Catalina", "Brody", "Freya", "Antonio", "Juliette", "Abel", "Sloane", "Alex", "June", "Tristan", "Sara", "Graham", "Ada", "Zayden", "Kimberly", "Judah", "River", "Xander", "Ember", "Miguel", "Juliana", "Atlas", "Aliyah", "Messiah", "Millie", "Barrett", "Brynlee", "Tucker", "Teagan", "Timothy", "Morgan", "Alan", "Jordyn", "Edward", "London", "Leon", "Alaina", "Dawson", "Olive", "Eric", "Rosalie", "Ace", "Alyssa", "Victor", "Ariel", "Abraham", "Finley", "Nicolas", "Arabella", "Jesse", "Journee", "Charlie", "Hope", "Patrick", "Leila", "Walker", "Alana", "Joel", "Gemma", "Richard", "Vanessa", "Beckett", "Gracie", "Blake", "Noelle", "Alejandro", "Marley", "Avery", "Elise", "Grant", "Presley", "Peter", "Kamila", "Oscar", "Zara", "Matias", "Amy", "Amari", "Kayla", "Lukas", "Payton", "Andres", "Blake", "Arlo", "Ruth", "Colt", "Alani", "Adonis", "Annabelle", "Kyrie", "Sage", "Steven", "Aspen", "Felix", "Laila", "Preston", "Lila", "Marcus", "Rachel", "Holden", "Trinity", "Emilio", "Daniela", "Remington", "Alexa", "Jeremy", "Lilly", "Kaleb", "Lauren", "Brantley", "Elsie", "Bryce", "Margot", "Mark", "Adelyn", "Knox", "Zuri", "Israel", "Brooke", "Phoenix", "Sawyer", "Kobe", "Lilah", "Nash", "Lola", "Griffin", "Selena", "Caden", "Mya", "Kenneth", "Sydney", "Kyler", "Diana", "Hayes", "Ana", "Jax", "Vera", "Rafael", "Alayna", "Beckham", "Nyla", "Javier", "Elaina", "Maximus", "Rebecca", "Simon", "Angela", "Paul", "Kali", "Omar", "Alivia", "Kaden", "Raegan", "Kash", "Rowan", "Lane", "Phoebe", "Bryan", "Camilla", "Riley", "Joanna", "Zane", "Malia", "Louis", "Vivienne", "Aidan", "Dakota", "Paxton", "Brooklynn", "Maximiliano", "Evangeline", "Karson", "Camille", "Cash", "Jane", "Cayden", "Nicole", "Emerson", "Catherine", "Tobias", "Jocelyn", "Ronan", "Julianna", "Brian", "Lena", "Dallas", "Lucille", "Bradley", "Mckenna", "Jorge", "Paige", "Walter", "Adelaide", "Josue", "Charlee", "Khalil", "Mariana", "Damien", "Myla", "Jett", "Mckenzie", "Kairo", "Tessa", "Zander", "Miriam", "Andre", "Oakley", "Cohen", "Kailani", "Crew", "Alayah", "Hendrix", "Amira", "Colin", "Adaline", "Chance", "Phoenix", "Malakai", "Milani", "Clayton", "Annie", "Daxton", "Lia", "Malcolm", "Angelina", "Lennox", "Harley", "Martin", "Cali", "Jaden", "Maggie", "Kayson", "Hayden", "Bodhi", "Leia", "Francisco", "Fiona", "Cody", "Briella", "Erick", "Journey", "Kameron", "Lennon", "Atticus", "Saylor", "Dante", "Jayla", "Jensen", "Kaia", "Cruz", "Thea", "Finley", "Adriana", "Brady", "Mariah", "Joaquin", "Juliet", "Anderson", "Oaklynn", "Gunner", "Kiara", "Muhammad", "Alexis", "Zayn", "Haven", "Derek", "Aniyah", "Raymond", "Delaney", "Kyle", "Gracelynn", "Angelo", "Kendall", "Reid", "Winter", "Spencer", "Lilith", "Nico", "Logan", "Jaylen", "Amiyah", "Jake", "Evie", "Prince", "Alexandria", "Manuel", "Gracelyn", "Ali", "Gabriela", "Gideon", "Sutton", "Stephen", "Harlow", "Ellis", "Madilyn", "Orion", "Makayla", "Rylan", "Evelynn", "Eduardo", "Gia", "Mario", "Nina", "Rory", "Amina", "Cristian", "Giselle", "Odin", "Brynn", "Tanner", "Blair", "Julius", "Amari", "Callum", "Octavia", "Sean", "Michelle", "Kane", "Talia", "Ricardo", "Demi", "Travis", "Alaya", "Wade", "Kaylani", "Warren", "Izabella", "Fernando", "Fatima", "Titus", "Tatum", "Leonel", "Makenzie", "Edwin", "Lilliana", "Cairo", "Arielle", "Corbin", "Palmer", "Dakota", "Melissa", "Ismael", "Willa", "Colson", "Samara", "Killian", "Destiny", "Major", "Dahlia", "Tate", "Celeste", "Gianni", "Ainsley", "Elian", "Rylie", "Remy", "Reign", "Lawson", "Laura", "Niko", "Adelynn", "Nasir", "Gabrielle", "Kade", "Remington", "Armani", "Wren", "Ezequiel", "Brinley", "Marshall", "Amora", "Hector", "Lainey", "Desmond", "Collins", "Kason", "Lexi", "Garrett", "Aitana", "Jared", "Alessandra", "Cyrus", "Kenzie", "Russell", "Raelyn", "Cesar", "Elle", "Tyson", "Everlee", "Malik", "Haisley", "Donovan", "Hallie", "Jaxton", "Wynter", "Cade", "Daleyza", "Romeo", "Gwendolyn", "Nehemiah", "Paislee", "Sergio", "Ariyah", "Iker", "Veronica", "Caiden", "Heidi", "Jay", "Anaya", "Pablo", "Cataleya", "Devin", "Kira", "Jeffrey", "Avianna", "Otto", "Felicity", "Kamari", "Aylin", "Ronin", "Miracle", "Johnny", "Sabrina", "Clark", "Lana", "Ari", "Ophelia", "Marco", "Elianna", "Edgar", "Royalty", "Bowen", "Madeleine", "Jaiden", "Esmeralda", "Grady", "Joy", "Zayne", "Kalani", "Sullivan", "Esme", "Jayceon", "Jessica", "Sterling", "Leighton", "Andy", "Ariah", "Conor", "Makenna", "Raiden", "Nylah", "Royal", "Viviana", "Royce", "Camryn", "Solomon", "Cassidy", "Trevor", "Dream", "Winston", "Luciana", "Emanuel", "Maisie", "Finnegan", "Stevie", "Pedro", "Kate", "Luciano", "Lyric", "Harvey", "Daniella", "Franklin", "Alicia", "Noel", "Daphne", "Troy", "Frances", "Princeton", "Charli", "Johnathan", "Raven", "Erik", "Paris", "Fabian", "Nayeli", "Oakley", "Serena", "Rhys", "Heaven", "Porter", "Bianca", "Hugo", "Helen", "Frank", "Hattie", "Damon", "Averie", "Kendrick", "Mabel", "Mathias", "Selah", "Milan", "Allie", "Peyton", "Marlee", "Wilder", "Kinley", "Callan", "Regina", "Gregory", "Carmen", "Seth", "Jennifer", "Matthias", "Jordan", "Briggs", "Alison", "Ibrahim", "Stephanie", "Roberto", "Maren", "Conner", "Kayleigh", "Quinn", "Angel", "Kashton", "Annalise", "Sage", "Jacqueline", "Santino", "Braelynn", "Kolton", "Emory", "Alijah", "Rosemary", "Dominick", "Scarlet", "Zyaire", "Amanda", "Apollo", "Danielle", "Kylo", "Emelia", "Reed", "Ryan", "Philip", "Carolina", "Kian", "Astrid", "Shawn", "Kensley", "Kaison", "Shiloh", "Leonidas", "Maci", "Ayaan", "Francesca", "Lucca", "Rory", "Memphis", "Celine", "Ford", "Kamryn", "Baylor", "Zariah", "Kyson", "Liana", "Uriel", "Poppy", "Allen", "Maliyah", "Collin", "Keira", "Ruben", "Skyler", "Archie", "Noa", "Dalton", "Skye", "Esteban", "Nadia", "Adan", "Addilyn", "Forrest", "Rosie", "Alonzo", "Eve", "Isaias", "Sarai", "Leland", "Edith", "Jase", "Jolene", "Dax", "Maddison", "Kasen", "Meadow", "Gage", "Charleigh", "Kamden", "Matilda", "Marcos", "Elliott", "Jamison", "Madelynn", "Francis", "Holly", "Hank", "Leona", "Alexis", "Azalea", "Tripp", "Katie", "Frederick", "Mira", "Jonas", "Ari", "Stetson", "Kaitlyn", "Cassius", "Danna", "Izaiah", "Cameron", "Eden", "Kyla", "Maximilian", "Bristol", "Rocco", "Kora", "Tatum", "Armani", "Keegan", "Nia", "Aziel", "Malani", "Moses", "Dylan", "Bruce", "Remy", "Lewis", "Maia", "Braylen", "Dior", "Omari", "Legacy", "Mack", "Alessia", "Augustus", "Shelby", "Enrique", "Maryam", "Armando", "Sylvia", "Pierce", "Yaretzi", "Moises", "Lorelei", "Asa", "Madilynn", "Shane", "Abby", "Emmitt", "Helena", "Soren", "Jimena", "Dorian", "Elisa", "Keanu", "Renata", "Zaiden", "Amber", "Raphael", "Aviana", "Deacon", "Carter", "Abdiel", "Emmy", "Kieran", "Haley", "Phillip", "Alondra", "Ryland", "Elaine", "Zachariah", "Erin", "Casey", "April", "Zaire", "Emely", "Albert", "Imani", "Baker", "Kennedi", "Corey", "Lorelai", "Kylan", "Hanna", "Denver", "Kelsey", "Gunnar", "Aurelia", "Jayson", "Colette", "Drew", "Jaliyah", "Callen", "Kylee", "Jasiah", "Macie", "Drake", "Aisha", "Kannon", "Dorothy", "Braylon", "Charley", "Sonny", "Kathryn", "Bo", "Adelina", "Moshe", "Adley", "Huxley", "Monroe", "Quentin", "Sierra", "Rowen", "Ailani", "Santana", "Miranda", "Cannon", "Mikayla", "Kenzo", "Alejandra", "Wells", "Amirah", "Julio", "Jada", "Nikolai", "Jazlyn", "Conrad", "Jenna", "Jalen", "Jayleen", "Makai", "Beatrice", "Benson", "Kendra", "Derrick", "Lyra", "Gerardo", "Nola", "Davis", "Emberly", "Abram", "Mckinley", "Mohamed", "Myra", "Ronald", "Katalina", "Raul", "Antonella", "Arjun", "Zelda", "Dexter", "Alanna", "Kaysen", "Amaia", "Jaime", "Priscilla", "Scott", "Briar", "Lawrence", "Kaliyah", "Ariel", "Itzel", "Skyler", "Oaklyn", "Danny", "Alma", "Roland", "Mallory", "Chandler", "Novah", "Yusuf", "Amalia", "Samson", "Fernanda", "Case", "Alia", "Zain", "Angelica", "Roy", "Elliot", "Rodrigo", "Justice", "Sutton", "Mae", "Boone", "Cecelia", "Saint", "Gloria", "Saul", "Ariya", "Jaziel", "Virginia", "Hezekiah", "Cheyenne", "Alec", "Aleah", "Arturo", "Jemma", "Jamari", "Henley", "Jaxtyn", "Meredith", "Julien", "Leyla", "Koa", "Lennox", "Reece", "Ensley", "Landen", "Zahra", "Koda", "Reina", "Darius", "Frankie", "Sylas", "Lylah", "Ares", "Nalani", "Kyree", "Reyna", "Boston", "Saige", "Keith", "Ivanna", "Taylor", "Aleena", "Johan", "Emerie", "Edison", "Ivory", "Sincere", "Leslie", "Watson", "Alora", "Jerry", "Ashlyn", "Nikolas", "Bethany", "Quincy", "Bonnie", "Shepherd", "Sasha", "Brycen", "Xiomara", "Marvin", "Salem", "Dariel", "Adrianna", "Axton", "Dayana", "Donald", "Clementine", "Bodie", "Karina", "Finnley", "Karsyn", "Onyx", "Emmie", "Rayan", "Julie", "Raylan", "Julieta", "Brixton", "Briana", "Colby", "Carly", "Shiloh", "Macy", "Valentino", "Marie", "Layton", "Oaklee", "Trenton", "Christina", "Landyn", "Malaysia", "Alessandro", "Ellis", "Ahmad", "Irene", "Gustavo", "Anne", "Ledger", "Anahi", "Ridge", "Mara", "Ander", "Rhea", "Ahmed", "Davina", "Kingsley", "Dallas", "Issac", "Jayda", "Mauricio", "Mariam", "Tony", "Skyla", "Leonard", "Siena", "Mohammed", "Elora", "Uriah", "Marilyn", "Duke", "Jazmin", "Kareem", "Megan", "Lucian", "Rosa", "Marcelo", "Savanna", "Aarav", "Allyson", "Leandro", "Milan", "Reign", "Coraline", "Clay", "Johanna", "Kohen", "Melany", "Dennis", "Chelsea", "Samir", "Michaela", "Ermias", "Melina", "Otis", "Angie", "Emir", "Cassandra", "Nixon", "Yara", "Ty", "Kassidy", "Sam", "Liberty", "Fletcher", "Lilian", "Wilson", "Avah", "Dustin", "Anya", "Hamza", "Laney", "Bryant", "Navy", "Flynn", "Opal", "Lionel", "Amani", "Mohammad", "Zaylee", "Cason", "Mina", "Jamir", "Sloan", "Aden", "Romina", "Dakari", "Ashlynn", "Justice", "Aliza", "Dillon", "Liv", "Layne", "Malaya", "Zaid", "Blaire", "Alden", "Janelle", "Nelson", "Kara", "Devon", "Analia", "Titan", "Hadassah", "Chris", "Hayley", "Khari", "Karla", "Zeke", "Chaya", "Noe", "Cadence", "Alberto", "Kyra", "Roger", "Alena", "Brock", "Ellianna", "Rex", "Katelyn", "Quinton", "Kimber", "Alvin", "Laurel", "Cullen", "Lina", "Azariah", "Capri", "Harlan", "Braelyn", "Kellan", "Faye", "Lennon", "Kamiyah", "Marcel", "Kenna", "Keaton", "Louise", "Morgan", "Calliope", "Ricky", "Kaydence", "Trey", "Nala", "Karsyn", "Tiana", "Langston", "Aileen", "Miller", "Sunny", "Chaim", "Zariyah", "Salvador", "Milana", "Amias", "Giuliana", "Tadeo", "Eileen", "Curtis", "Elodie", "Lachlan", "Rayna", "Amos", "Monica", "Anakin", "Galilea", "Krew", "Journi", "Tomas", "Lara", "Jefferson", "Marina", "Yosef", "Aliana", "Bruno", "Harmoni", "Korbin", "Jamie", "Augustine", "Holland", "Cayson", "Emmalyn", "Mathew", "Lauryn", "Vihaan", "Chanel", "Jamie", "Tinsley", "Clyde", "Jessie", "Brendan", "Lacey", "Jagger", "Elyse", "Carmelo", "Janiyah", "Harry", "Jolie", "Nathanael", "Ezra", "Mitchell", "Marleigh", "Darren", "Roselyn", "Ray", "Lillie", "Jedidiah", "Louisa", "Jimmy", "Madisyn", "Lochlan", "Penny", "Bellamy", "Kinslee", "Eddie", "Treasure", "Rayden", "Zaniyah", "Reese", "Estella", "Stanley", "Jaylah", "Joe", "Khaleesi", "Houston", "Alexia", "Douglas", "Dulce", "Vincenzo", "Indie", "Casen", "Maxine", "Emery", "Waverly", "Joziah", "Giovanna", "Leighton", "Miley", "Marcellus", "Saoirse", "Atreus", "Estrella", "Aron", "Greta", "Hugh", "Rosalia", "Musa", "Mylah", "Tommy", "Teresa", "Alfredo", "Bridget", "Junior", "Kelly", "Neil", "Adalee", "Westley", "Aubrie", "Banks", "Lea", "Eliel", "Harlee", "Melvin", "Anika", "Maximo", "Itzayana", "Briar", "Hana", "Colten", "Kaisley", "Lance", "Mikaela", "Nova", "Naya", "Trace", "Avalynn", "Axl", "Margo", "Ramon", "Sevyn", "Vicente", "Florence", "Brennan", "Keilani", "Caspian", "Lyanna", "Remi", "Joelle", "Deandre", "Kataleya", "Legacy", "Royal", "Lee", "Averi", "Valentin", "Kallie", "Ben", "Winnie", "Louie", "Baylee", "Westin", "Martha", "Wayne", "Pearl", "Benicio", "Alaiya", "Grey", "Rayne", "Zayd", "Sylvie", "Gatlin", "Brylee", "Mekhi", "Jazmine", "Orlando", "Ryann", "Bjorn", "Kori", "Harley", "Noemi", "Alonso", "Haylee", "Rio", "Julissa", "Aldo", "Celia", "Byron", "Laylah", "Eliseo", "Rebekah", "Ernesto", "Rosalee", "Talon", "Aya", "Thaddeus", "Bria", "Brecken", "Adele", "Kace", "Aubrielle", "Kellen", "Tiffany", "Enoch", "Addyson", "Kiaan", "Kai", "Lian", "Bellamy", "Creed", "Leilany", "Rohan", "Princess", "Callahan", "Chana", "Jaxxon", "Estelle", "Ocean", "Selene", "Crosby", "Sky", "Dash", "Dani", "Gary", "Thalia", "Mylo", "Ellen", "Ira", "Rivka", "Magnus", "Amelie", "Salem", "Andi", "Abdullah", "Kynlee", "Kye", "Raina", "Tru", "Vienna", "Forest", "Alianna", "Jon", "Livia", "Misael", "Madalyn", "Madden", "Mercy", "Braden", "Novalee", "Carl", "Ramona", "Hassan", "Vada", "Emory", "Berkley", "Kristian", "Gwen", "Alaric", "Persephone", "Ambrose", "Milena", "Dario", "Paula", "Allan", "Clare", "Bode", "Kairi", "Boden", "Linda", "Juelz", "Paulina", "Kristopher", "Kamilah", "Genesis", "Amoura", "Idris", "Hunter", "Ameer", "Isabela", "Anders", "Karen", "Darian", "Marianna", "Kase", "Sariyah", "Aryan", "Theodora", "Dane", "Annika", "Guillermo", "Kyleigh", "Elisha", "Nellie", "Jakobe", "Scarlette", "Thatcher", "Keyla", "Eugene", "Kailey", "Ishaan", "Mavis", "Larry", "Lilianna", "Wesson", "Rosalyn", "Yehuda", "Sariah", "Alvaro", "Tori", "Bobby", "Yareli", "Bronson", "Aubriella", "Dilan", "Bexley", "Kole", "Bailee", "Kyro", "Jianna", "Tristen", "Keily", "Blaze", "Annabella", "Brayan", "Azariah", "Jadiel", "Denisse", "Kamryn", "Promise", "Demetrius", "August", "Maurice", "Hadlee", "Arian", "Halle", "Kabir", "Fallon", "Rocky", "Oakleigh", "Rudy", "Zaria", "Randy", "Jaylin", "Rodney", "Paisleigh", "Yousef", "Crystal", "Felipe", "Ila", "Robin", "Aliya", "Aydin", "Cynthia", "Dior", "Giana", "Kaiser", "Maleah", "Van", "Rylan", "Brodie", "Aniya", "London", "Denise", "Eithan", "Emmeline", "Stefan", "Scout", "Ulises", "Simone", "Camilo", "Noah", "Branson", "Zora", "Jakari", "Meghan", "Judson", "Landry", "Yahir", "Ainhoa", "Zavier", "Lilyana", "Damari", "Noor", "Jakob", "Belen", "Jaxx", "Brynleigh", "Bentlee", "Cleo", "Cain", "Meilani", "Niklaus", "Karter", "Rey", "Amaris", "Zahir", "Frida", "Aries", "Iliana", "Blaine", "Violeta", "Kyng", "Addisyn", "Castiel", "Nancy", "Henrik", "Denver", "Joey", "Leanna", "Khalid", "Braylee", "Bear", "Kiana", "Graysen", "Wrenley", "Jair", "Barbara", "Kylen", "Khalani", "Darwin", "Aspyn", "Alfred", "Ellison", "Ayan", "Judith", "Kenji", "Robin", "Zakai", "Valery", "Avi", "Aila", "Cory", "Deborah", "Fisher", "Cara", "Jacoby", "Clarissa", "Osiris", "Iyla", "Harlem", "Lexie", "Jamal", "Anais", "Santos", "Kaylie", "Wallace", "Nathalie", "Brett", "Alisson", "Fox", "Della", "Leif", "Addilynn", "Maison", "Elsa", "Reuben", "Zoya", "Adler", "Layne", "Zev", "Marlowe", "Calum", "Jovie", "Kelvin", "Kenia", "Zechariah", "Samira", "Bridger", "Jaylee", "Mccoy", "Jenesis", "Seven", "Etta", "Shepard", "Shay", "Azrael", "Amayah", "Leroy", "Avayah", "Terry", "Egypt", "Harold", "Flora", "Mac", "Raquel", "Mordechai", "Whitney", "Ahmir", "Zola", "Cal", "Giavanna", "Franco", "Raya", "Trent", "Veda", "Blaise", "Halo", "Coen", "Paloma", "Dominik", "Nataly", "Marley", "Whitley", "Davion", "Dalary", "Jeremias", "Drew", "Riggs", "Guadalupe", "Jones", "Kamari", "Will", "Esperanza", "Damir", "Loretta", "Dangelo", "Malayah", "Canaan", "Natasha", "Dion", "Stormi", "Jabari", "Ansley", "Landry", "Carolyn", "Salvatore", "Corinne", "Kody", "Paola", "Hakeem", "Brittany", "Truett", "Emerald", "Gerald", "Freyja", "Lyric", "Zainab", "Gordon", "Artemis", "Jovanni", "Jillian", "Kamdyn", "Kimora", "Alistair", "Zoie", "Cillian", "Aislinn", "Foster", "Emmaline", "Terrance", "Ayleen", "Murphy", "Queen", "Zyair", "Jaycee", "Cedric", "Murphy", "Rome", "Nyomi", "Abner", "Elina", "Colter", "Hadleigh", "Dayton", "Marceline", "Jad", "Marisol", "Xzavier", "Yasmin", "Rene", "Zendaya", "Vance", "Chandler", "Duncan", "Emani", "Frankie", "Jaelynn", "Bishop", "Kaiya", "Davian", "Nathalia", "Everest", "Violette", "Heath", "Joyce", "Jaxen", "Paityn", "Marlon", "Elisabeth", "Maxton", "Emmalynn", "Reginald", "Luella", "Harris", "Yamileth", "Jericho", "Aarya", "Keenan", "Luisa", "Korbyn", "Zhuri", "Wes", "Araceli", "Eliezer", "Harleigh", "Jeffery", "Madalynn", "Kalel", "Melani", "Kylian", "Laylani", "Turner", "Magdalena", "Willie", "Mazikeen", "Rogelio", "Belle", "Ephraim", "Kadence"];

    //shuffle in a pseudo-random way according to the last name
    //each cognomen has its own pattern of praenomens
    var r = random(parseInt(lastName, 36));
    for (var i = 0; i < firstNames.length; i++) {
        var remaining = firstNames.length - i;
        var swapIndex = Math.floor(r() * remaining) + i;

        var swap = firstNames[swapIndex];
        firstNames[swapIndex] = firstNames[i];
        firstNames[i] = swap;
    }

    if (index < firstNames.length) return firstNames[index] + " " + lastName;
    else return firstNames[index % firstNames.length] + " " + lastName + " " + romanNumeral(Math.ceil(index / firstNames.length));
}

function romanNumeral(num) {
    var roman = "";
    if (num < 0) roman += "-";
    num = Math.abs(num);
    var numerals = Object.entries({ M: 1000, CM: 900, D: 500, CD: 400, C: 100, XC: 90, L: 50, XL: 40, X: 10, IX: 9, V: 5, IV: 4, I: 1 }), digit;
    while (num > 0) {
        numerals.forEach(entry => {
            if (num >= entry[1]) {
                roman += entry[0];
                num -= entry[1];
            }
        });
    }
    return roman;
}

function generateCognomen(hash) {
    var hashNum = parseInt(hash.slice(-5), 16);

    var name = "";

    var syllables = [
        ["din", "", "eln", "dor", "a", "smi", "thi", "pai", "an", "rot", "fe"],
        ["sen", "bra", "gou", "ger", "sek", "low", "sho", "ll", "roc", "fe"],
        ["son", "ner", "man", "maner", "", "alt", "ba", "lo", "ick", "an"]
    ];

    for (var i = 0; i < syllables.length; i++) {
        var rank = syllables[i];
        name += rank[hashNum % rank.length];
        hashNum /= rank.length;
        hashNum = Math.floor(hashNum);
    }

    return name.charAt(0).toUpperCase() + name.substring(1).toLowerCase();

}

function readJsonFile(file) {
    if(!fs.existsSync(file)) return {};
    return JSON.parse(fs.readFileSync(file).toString());
}

function random(seed) {
    if (seed % Math.PI == 0) seed++;

    return function () {
        var x = Math.sin(seed++) * 10000;
        seed = x;
        return x - Math.floor(x);
    }
}