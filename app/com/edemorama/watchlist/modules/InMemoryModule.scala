package com.edemorama.watchlist.modules

import com.edemorama.watchlist.services.InMemoryServiceActor
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class InMemoryModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[InMemoryServiceActor](InMemoryServiceActor.name)
  }
}
