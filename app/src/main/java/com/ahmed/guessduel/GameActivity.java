TextView display = findViewById(R.id.inputDisplay);
StringBuilder inputVal = new StringBuilder();

int[] btns = {
    R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,
    R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9
};

for (int id : btns) {
    Button b = findViewById(id);
    b.setOnClickListener(v -> {
        if (inputVal.length() < 3) {
            inputVal.append(b.getText());
            display.setText(inputVal.toString());
        }
    });
}

findViewById(R.id.btnDel).setOnClickListener(v -> {
    if (inputVal.length() > 0) {
        inputVal.deleteCharAt(inputVal.length() - 1);
        display.setText(inputVal.length()==0 ? "0" : inputVal.toString());
    }
});

findViewById(R.id.btnOk).setOnClickListener(v -> {

    if (inputVal.length() == 0) return;

    int g = Integer.parseInt(inputVal.toString());

    inputVal.setLength(0);
    display.setText("0");

    handleGuessValue(g);
});
