#if __has_include(<RCTText/RCTBaseTextInputView.h>)
#import <RCTText/RCTBaseTextInputView.h>
#else
#import "RCTBaseTextInputView.h"
#endif

NS_ASSUME_NONNULL_BEGIN

@interface RNSelectableTextView : RCTBaseTextInputView

@property (nonnull, nonatomic, copy) NSString *value;
@property (nonatomic, copy) RCTDirectEventBlock onSelection;
@property (nonatomic, copy) RCTDirectEventBlock onSelectionCallback;
@property (nullable, nonatomic, copy) NSArray<NSString *> *menuItems;
@property (nonatomic, copy) RCTDirectEventBlock onHighlightPress;
@property (nullable, nonatomic, copy) NSArray<NSDictionary *> *highlights;

@end

NS_ASSUME_NONNULL_END