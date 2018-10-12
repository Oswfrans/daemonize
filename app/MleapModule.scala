import com.google.inject.AbstractModule
import ml.combust.bundle.BundleFile
import ml.combust.mleap.runtime.MleapSupport._
import ml.combust.mleap.runtime.frame.Transformer
import resource._


class MleapModule extends AbstractModule {
  def configure(): Unit = {
    val mlModelPath = this.getClass.getClassLoader.getResource("ml-model.zip").getPath
    val zipBundleM = (for(bundle <- managed(BundleFile(s"jar:file:$mlModelPath"))) yield {
      bundle.loadMleapBundle().get
    }).opt.get
    val mleapPipeline = zipBundleM.root

    bind(classOf[Transformer]).toInstance(mleapPipeline)
  }
}
