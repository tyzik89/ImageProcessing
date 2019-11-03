package models.notification;

//Наблюдаемый
public interface Observable {

    //Регистрация слушателей
    void registerObserver(Observer observer);

    //Уведомление слушателей
    void notifyObservers(String message);
}
