
# Getting started

Склонируйте репозиторий
```bash
git clone https://github.com/Svazars/SPRO.git
```

Проверьте, что у вас установлена java подходящей версии:
```bash
java -ea -esa -jar ./SPRO/out/artifacts/SPRO_jar/SPRO.jar
```
должен напечататься help для эмулятора.


## Assembler

Создайте свою очень простую программу `simple.asm`
```
echo "halt" > simple.asm
```

Превратите её в "машинный код"
```
java -ea -esa -jar ./SPRO/out/artifacts/SPRO_jar/SPRO.jar simple.asm
```
и в текущей директории появится output.bin

## Run program

Подсоедините к процессору 256 байт памяти, загрузите по адресу ноль свою программу и дайте ей исполняться не более чем 100 тактов:
```
java -ea -esa -jar ./SPRO/out/artifacts/SPRO_jar/SPRO.jar output.bin exec 100 256
```

## Debugger

Подсоедините к процессору 256 байт памяти, загрузите по адресу ноль свою программу и поотлаживайтесь в интерактивном режиме:

```
java -ea -esa -jar ./SPRO/out/artifacts/SPRO_jar/SPRO.jar output.bin debug-novis 256
```

Напечатайте help и появится справка по поддерживаемым командам.
