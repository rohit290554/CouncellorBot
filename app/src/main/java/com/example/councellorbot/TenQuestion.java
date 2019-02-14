package com.example.councellorbot;

public class TenQuestion {

    public String questions[] = {
            "Name of the instrument to measure atomspheric pressure ?",
            "Which instrument is used in submarine to see the objects above sea level ?",
            "Which instrument is used to measure altitudes in aircraft's ?",
            "The power of a lens is measured in :",
            "Who is regarded as father of modern chemistry ?",
            "Which is not a type of elements ?",
            "Which acid is present in lemon?",
            "Bowman’s Capsule’ works as a part of the functional unit of which among the following human physiological system?",
            "Plants which are adapted to grow in soils containing high concentration of salt are known as:",
            "Which among the following have a ‘mixed heart’ ,that is the heart in which the oxygenated and the deoxygenated blood is mixed?"
    };

    public String choices[][] = {
            {"Barometer","Barograph","Bolometer","Callipers"},
            {"Pykometer","Polygraph","Photometer","Periscope"},
            {"Audiometer","Ammeter","Altimeter","Anemometer"},
            { "diopters","aeon","lumen","candela"},
            {"Ruterford","Einstein","Lavoisier","C.V. Raman"},
            {"Metals","Non Metals","Metalloids","Gases"},
            {"marlic acid","citric acid","lactic acid","tartaric acid"},
            {"Circulatory System","Respiratory System","Excretory System","Reproductive System"},
            {"Xerophytes","Mesophytes","Halophytes","Thallophytes"},
            {"Birds","Fishes","Reptiles","Nematodes"}

    };

    public String correctAnswer[] = {
            "Barograph",
            "Pykometer",
            "Altimeter",
            "diopters",
            "Lavoisier",
            "Gases",
            "citric acid",
            "Excretory System",
            "Halophytes",
            "Reptiles"
    };

    public String getQuestion(int a){
        String question = questions[a];
        return question;
    }

    public String getchoice1(int a){
        String choice = choices[a][0];
        return choice;
    }

    public String getchoice2(int a){
        String choice = choices[a][1];
        return choice;
    }

    public String getchoice3(int a){
        String choice = choices[a][2];
        return choice;
    }

    public String getchoice4(int a){
        String choice = choices[a][3];
        return choice;
    }

    public String getCorrectAnswer(int a){
        String answer = correctAnswer[a];
        return answer;
    }

}
