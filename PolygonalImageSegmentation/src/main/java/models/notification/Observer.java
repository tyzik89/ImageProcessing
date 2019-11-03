package models.notification;

//Наблюдатель
public interface Observer {

    //Функция, которая срабатывает при поступлении уведомления
    void notification(String message);
}
