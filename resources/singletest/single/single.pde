
Wolfie[] wolfies = new Wolfie[1];

 void setup(){

  background(255);
  reset();
}

 void draw(){
  fill(color(0, 0, 128));
  textSize(50);
  text("Press spacebar to make it more cosy in here!", 95, 50);
  textSize(20);
  text("Press 'r' to shut down the party...", 450, 100);
}

 void keyPressed(){
  if (key == ' '){
    reset();
  }
  if (key == 'r'){
    background(255);
  }
}

 void reset(){
    for (int i = 0; i < wolfies.length; i++){
    wolfies[i] = new Wolfie(random(1200), random(720), color(random(255), random(255), random(255)), color(random(255), random(255), random(255)));
      wolfies[i].display();
  }
}
class Wolfie {
  float xPos;
  float yPos;
  int eyeColor;
  int innerEarColor;

  Wolfie(float initX, float initY, int initEyeColor, int initInnerEarColor){
    xPos = initX;
    yPos = initY;
    eyeColor = initEyeColor;
    innerEarColor = initInnerEarColor;

   }

   void display(){
  stroke(0);
  strokeWeight(5);

  //ears
  fill(160,82,45);
  triangle(xPos -130, yPos - 56, xPos - 125, yPos -177, xPos - 62, yPos -109);
  triangle(xPos +130, yPos - 56, xPos + 125, yPos -177, xPos + 62, yPos -109);

  //interear
  noStroke();
  fill(innerEarColor);
  triangle(xPos -119, yPos - 72, xPos - 116, yPos - 145, xPos - 80, yPos -102);
  triangle(xPos +119, yPos - 72, xPos + 116, yPos - 145, xPos + 80, yPos -102);

  //head
  stroke(0);
  fill(160,82,45);
  ellipse(xPos, yPos, 300,250);

  //eyes
  fill(eyeColor);
  ellipse(xPos - 60, yPos -35, 110, 110);
  ellipse(xPos + 60, yPos -35, 110, 110);

  fill(0);
  ellipse(xPos - 55, yPos -35, 70, 70);
  ellipse(xPos + 55, yPos -35, 70, 70);

  fill(255);
  noStroke();
  pushMatrix();
  translate(xPos - 45, yPos -50);
  rotate(0.5f);
  ellipse(0, 0, 30, 20);
  popMatrix();
  pushMatrix();
  translate(xPos + 65, yPos -50);
  rotate(0.5f);
  ellipse(0, 0, 30, 20);
  popMatrix();

  //undernose
  fill(245,222,179);
  ellipse(xPos, yPos + 70, 110, 80);

  //fangs
  stroke(0);
  strokeWeight(3);
  fill(255);
  triangle(xPos + 16, yPos + 94, xPos + 27, yPos +111, xPos + 28, yPos + 88);
  triangle(xPos - 16, yPos + 94, xPos - 27, yPos +111, xPos - 28, yPos + 88);

  //line
  strokeWeight(6);
  stroke(128,0,0);
  strokeCap(SQUARE);
  line(xPos, yPos + 30, xPos, yPos + 95);

  //nose
  stroke(0);
  fill(128,0,0);
  ellipse(xPos, yPos + 35, 55, 35);

  //mouth
  noFill();
  strokeCap(ROUND);
  arc(xPos, yPos + 75, 75, 40, 0.2f, PI-0.2f);
}}
