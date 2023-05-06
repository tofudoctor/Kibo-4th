# 開發筆記

> 用來記錄我幹了啥發生甚麼問題我怎麼解決 (或沒有解決)

> 一起編輯 -> OK

> 另外開 -> OK

> 要記住長頸鹿會使用魔法

*230507*

1.
```
// before going to the point, call this fuction to make simulator record
api.notifyGoingToGoal();
```
目前不確定這行程式碼的用處，但是 PGManual 說要加

2.
```
// get a camera image
Mat image = api.getMatNavCam();
api.saveMatImage(image, "Navcam-ori:P1");
```
要記得拍了照片要存檔， MAT 檔丟在 LINE 或副檔名改 JPG, PNG 就可以正常顯示不需要 OPENCV (MD 我搞很久ㄟ)

