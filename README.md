# OOP-Final-Project
## Books.java
### Instance variable
*拿掉 String dateAvaiToBorrow</br>
*加入 Date returnDate</br>
	紀錄還書的時間，型別是Date</br>
*加入 byte changeBranch</br>
    當書要在館與館之間移動時，它紀錄了書將會被異動到哪一館</br>

## Date.java
### Instance method
*加入 public int difference(Date d)</br>
	計算呼叫此方法的物件日期和d物件日期之間的差距</br>

## Library.java
### Instance variable
*加入 private Queue<Books> queue</br>
	裡面放的是那些將要被調館的書</br>
*加入 private Queue<Books> borrowQueue</br>
	只要被借出去的書都會放在queue裡，方便我們檢查有沒有使用者逾期未還書，以便即時更新使用者借書的權限</br>
### Instance method
*加入 private void routine()</br>
	把要換館的書放到目的地以及檢查有沒有使用者逾期未還書，即時更新使用者的借書權限</br>
*加入 private void updateReserveInfo(Books b)</br>
	目前只用在borrow裡面，目的是更新預約者們的資訊，檢查預約者是不是逾期還沒來取書，是的話就取消該預約者的預約權限90天，並取消他對此書的預約</br>
*borrow有做不少的更動，主要是跟reserve協調</br>
