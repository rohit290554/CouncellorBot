package com.example.councellorbot;



public class EngineeringQuestion {

    public String questions[] = {
            "Name of the instrument to measure atomspheric pressure ?",
            "Which instrument is used in submarine to see the objects above sea level ?",
            "Which instrument is used to measure altitudes in aircraft's ?",
            "The power of a lens is measured in :",
            "Who is regarded as father of modern chemistry ?",
            "Which is not a type of elements ?",
            "Which acid is present in lemon?",
            "The average of first 50 natural numbers is",
            "The number of 3-digit numbers divisible by 6, is ",
            "What is 1004 divided by 2?"

    };

    public String choices[][] = {
            {"Barometer","Barograph","Bolometer","Callipers"},
            {"Pykometer","Polygraph","Photometer","Periscope"},
            {"Audiometer","Ammeter","Altimeter","Anemometer"},
            { "diopters","aeon","lumen","candela"},
            {"Ruterford","Einstein","Lavoisier","C.V. Raman"},
            {"Metals","Non Metals","Metalloids","Gases"},
            {"marlic acid","citric acid","lactic acid","tartaric acid"},
            {"25.30","25.5","25.00","12.25"},
            {"49","166","150","151"},
            {"52","502","520","5002"}
    };

    public String correctAnswer[] = {
            "Barograph",
            "Pykometer",
            "Altimeter",
            "diopters",
            "Lavoisier",
            "Gases",
            "citric acid",
            "25.5",
            "150",
            "502"
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
