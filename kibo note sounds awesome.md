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
api.saveMatImage(api.getMatNavCam(), "Navcam-ori:P1.png");
```
要記得拍了照片要存檔， MAT 檔丟在 LINE 或副檔名改 JPG, PNG 就可以正常顯示不需要 OPENCV (MD 我搞很久ㄟ)

要加副檔名！！! 不然要自己改

各種旋轉制轉換器
https://www.andre-gaschler.com/rotationconverter/

# 版本紀錄

## V1.0

- 先走到底下
- 再走到點
- 再回到底下
- 不停重複
- 結果：撞牆

## V1.1

- 微調第3個點跟牆的距離
- 結果：撞牆again
- 發現： Astrobee 是一個32cm的方塊（要算）

## V1.2

- 增加牆厚度
- 若撞牆 => 判斷在牆中心左邊還右邊，偏左放左，偏右放右
- 結果：依然撞牆
- 發現：Dockcam 似乎比 Navcam 方向優秀？

## v1.3
- 測試各個Target
- 結果：剩 P2 會撞牆
- 發現：Target 和 Point 不一樣
