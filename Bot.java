import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.ApiContextInitializer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class Bot extends TelegramLongPollingBot  {
    Main main = new Main();
    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try{
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }


    public void sendMsg(Message message, String text) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboardrowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("/subscribe");
        keyboardFirstRow.add("/unsubscribe");
        keyboardFirstRow.add("weather_now");
        keyboardrowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardrowList);
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        ///sendMessage.setReply(message.getMessageId());      ссылается на сообщение
        sendMessage.setText(text).setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        Calendar hour = new GregorianCalendar();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "weather_now":
                    try {
                        sendMsg(message, parsing(55.76, 37.68));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/start":
                    sendMsg(message, "Чтобы узнать погоду сейчас, отправляйте команду /weather_now"+"\n"+
                            "Если хотите подписать на рассылки отправляйте /subscribe"+"\n"+
                            "Если хотите отписать от рассылок отправляйте /unsubscribe"+"\n");
                    break;
                case "/subscribe":
                    main.Append(update.getMessage().getChatId().intValue(), "True");
                    sendMsg(message, "Вы успешно подписаны на рассылки");
                    break;
                case "/unsubscribe":
                    if (main.dictionary1.containsKey(update.getMessage().getChatId().intValue()) && main.dictionary1.get(
                            update.getMessage().getChatId().intValue()) == "True") {
                        sendMsg(message, "Вы успешно отписаны от рассылок");
                        main.Delete(update.getMessage().getChatId().intValue(), "False");
                    } else {
                        sendMsg(message, "Вы и так не подписаны на рассылки: ");
                    }
            }
        }
        if(Calendar.HOUR_OF_DAY == 12){
            for(HashMap.Entry<Integer, String> set :main.dictionary1.entrySet()){
                if(main.dictionary1.containsKey(set.getKey())){
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(set.getKey().longValue());
                    try{
                    sendMessage1.setText(parsing(55.76, 37.68));}
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String parsing(Double lat, Double lon) throws IOException {
        String string = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%.5f&lon=%.5f&mode=xml&units=metric&appid=a417a9afcbcad213b77ced6b64f2e589",
                lat,lon);
        Document doc = Jsoup.connect(string).get();
        Elements element = doc.getElementsByTag("temperature");
        String MaxTemp = element.attr("max");
        String MinTemp = element.attr("min");
        String LiveTemp = element.attr("value");
        Elements element1 = doc.getElementsByTag("feels_like");
        String Feel = element1.attr("value")+"°C";
        Elements element2 = doc.getElementsByTag("humidity");
        String Vlaga = element2.attr("value")+"%";
        Elements element3 = doc.getElementsByTag("speed");
        String Speed = element3.attr("value")+"м/с";
        String text = "Погода на улице ❄: "+ LiveTemp + "°C\n" + "Максимальная температура на сегодня \uD83C\uDF21: "+MaxTemp+
                "\n" + "Минимальная температура на сегодня \uD83C\uDF21: "+ MinTemp + "\n"+"Влажность воздуха \uD83D\uDCA7: "+
                Vlaga + "\n" + "Скорость ветра: "+Speed;
        return text;
    }
    public String getBotUsername(){
        return "MoscowWeatherInJavaBot";
    }

    public String getBotToken(){
        return "1467597034:AAEKguJQTA7GDDK_mBvQtvF7TERm0dnUC_E";
    }
}
